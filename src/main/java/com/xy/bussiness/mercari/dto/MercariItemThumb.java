package com.xy.bussiness.mercari.dto;

import com.xy.bussiness.mercari.mybean.ItemRecord;
import com.xy.bussiness.notification.wechat.NotifyImageUrls;
import lombok.Data;

@Data
public class MercariItemThumb {
    private String mercariItemId;
    private String mercariItemTitle;
    private String imageUrl;

    public static String buildImageUrl(ItemRecord record) {
        return NotifyImageUrls.resolveMercariHtmlImageUrl(
                record.getMercariItemId(), record.getItemType(), record.getImageUrl());
    }
}
