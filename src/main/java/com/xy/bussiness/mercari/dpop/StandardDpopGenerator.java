package com.xy.bussiness.mercari.dpop;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class StandardDpopGenerator {

    private static final int P256_COORDINATE_SIZE = 32;
    private static final long CACHE_TTL_MS = TimeUnit.DAYS.toMillis(1);

    private KeyPair keyPair;
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        keyPair = keyPairGenerator.generateKeyPair();
    }

    public String generate(String httpMethod, String targetUri) {
        String cacheKey = httpMethod.toUpperCase() + " " + targetUri;
        long now = System.currentTimeMillis();
        CacheEntry entry = cache.get(cacheKey);
        if (entry != null && entry.isValid(now)) {
            return entry.dpop;
        }
        synchronized (this) {
            entry = cache.get(cacheKey);
            if (entry != null && entry.isValid(now)) {
                return entry.dpop;
            }
            String dpop = generateUncached(httpMethod, targetUri);
            cache.put(cacheKey, new CacheEntry(dpop, now + CACHE_TTL_MS));
            return dpop;
        }
    }

    private String generateUncached(String httpMethod, String targetUri) {
        try {
            ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

            JSONObject jwk = new JSONObject();
            jwk.put("crv", "P-256");
            jwk.put("kty", "EC");
            jwk.put("x", base64Url(fixedLength(publicKey.getW().getAffineX())));
            jwk.put("y", base64Url(fixedLength(publicKey.getW().getAffineY())));

            JSONObject header = new JSONObject();
            header.put("typ", "dpop+jwt");
            header.put("alg", "ES256");
            header.put("jwk", jwk);

            JSONObject payload = new JSONObject();
            payload.put("jti", UUID.randomUUID().toString());
            payload.put("htm", httpMethod.toUpperCase());
            payload.put("htu", targetUri);
            payload.put("iat", System.currentTimeMillis() / 1000);

            String signingInput = base64Url(header.toJSONString().getBytes(StandardCharsets.UTF_8))
                    + "."
                    + base64Url(payload.toJSONString().getBytes(StandardCharsets.UTF_8));

            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(signingInput.getBytes(StandardCharsets.US_ASCII));

            return signingInput + "." + base64Url(derToJoseSignature(signature.sign()));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate standard DPoP proof", e);
        }
    }

    private byte[] derToJoseSignature(byte[] derSignature) {
        int offset = 0;
        if (derSignature[offset++] != 0x30) {
            throw new IllegalArgumentException("Invalid ECDSA DER signature");
        }
        offset = skipLength(derSignature, offset);

        byte[] r = readDerInteger(derSignature, offset);
        offset += derIntegerLength(derSignature, offset);
        byte[] s = readDerInteger(derSignature, offset);

        byte[] joseSignature = new byte[P256_COORDINATE_SIZE * 2];
        copyToFixedPart(r, joseSignature, 0);
        copyToFixedPart(s, joseSignature, P256_COORDINATE_SIZE);
        return joseSignature;
    }

    private int skipLength(byte[] data, int offset) {
        int lengthByte = data[offset++] & 0xff;
        if (lengthByte < 0x80) {
            return offset;
        }
        return offset + (lengthByte & 0x7f);
    }

    private int derIntegerLength(byte[] data, int offset) {
        if (data[offset++] != 0x02) {
            throw new IllegalArgumentException("Invalid ECDSA DER integer");
        }
        int lengthByte = data[offset++] & 0xff;
        if (lengthByte < 0x80) {
            return 2 + lengthByte;
        }
        int lengthLength = lengthByte & 0x7f;
        int length = 0;
        for (int i = 0; i < lengthLength; i++) {
            length = (length << 8) + (data[offset + i] & 0xff);
        }
        return 2 + lengthLength + length;
    }

    private byte[] readDerInteger(byte[] data, int offset) {
        if (data[offset++] != 0x02) {
            throw new IllegalArgumentException("Invalid ECDSA DER integer");
        }
        int lengthByte = data[offset++] & 0xff;
        int length;
        if (lengthByte < 0x80) {
            length = lengthByte;
        } else {
            int lengthLength = lengthByte & 0x7f;
            length = 0;
            for (int i = 0; i < lengthLength; i++) {
                length = (length << 8) + (data[offset++] & 0xff);
            }
        }
        byte[] value = new byte[length];
        System.arraycopy(data, offset, value, 0, length);
        return value;
    }

    private void copyToFixedPart(byte[] value, byte[] target, int targetOffset) {
        int firstNonZero = 0;
        while (firstNonZero < value.length - 1 && value[firstNonZero] == 0) {
            firstNonZero++;
        }
        int valueLength = value.length - firstNonZero;
        int copyLength = Math.min(valueLength, P256_COORDINATE_SIZE);
        int sourceOffset = firstNonZero + valueLength - copyLength;
        int destinationOffset = targetOffset + P256_COORDINATE_SIZE - copyLength;
        System.arraycopy(value, sourceOffset, target, destinationOffset, copyLength);
    }

    private byte[] fixedLength(BigInteger value) {
        byte[] bytes = value.toByteArray();
        byte[] fixed = new byte[P256_COORDINATE_SIZE];
        copyToFixedPart(bytes, fixed, 0);
        return fixed;
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static final class CacheEntry {
        private final String dpop;
        private final long expiresAt;

        private CacheEntry(String dpop, long expiresAt) {
            this.dpop = dpop;
            this.expiresAt = expiresAt;
        }

        private boolean isValid(long now) {
            return now < expiresAt;
        }
    }
}
