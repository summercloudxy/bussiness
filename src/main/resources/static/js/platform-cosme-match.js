(function (window) {
    'use strict';

    var config = null;
    var pendingCosmeMatches = [];
    var cosmeMatchModalMode = 'batch';

    function apiUrl(path) {
        return config.apiPrefix + path;
    }

    function getBrandValue() {
        var brand = $(config.brandSelect + ' option:selected').val() || '';
        return brand === '全部' ? '' : brand;
    }

    function reloadList() {
        if (typeof config.reloadList === 'function') {
            config.reloadList();
        }
    }

    function openCosmeBatchMatchModal() {
        $('input[name="cosmeBatchMode"][value="limit"]').prop('checked', true);
        $('#cosmeBatchLimit').val(30).prop('disabled', false);
        $('#cosmeBatchSubmitBtn').prop('disabled', false).text('开始匹配');
        $('#cosmeBatchMatchModal').addClass('open');
    }

    function closeCosmeBatchMatchModal() {
        $('#cosmeBatchMatchModal').removeClass('open');
    }

    function onCosmeBatchModeChange() {
        var isLimit = $('input[name="cosmeBatchMode"]:checked').val() === 'limit';
        $('#cosmeBatchLimit').prop('disabled', !isLimit);
    }

    function submitCosmeBatchMatch() {
        var mode = $('input[name="cosmeBatchMode"]:checked').val();
        var limit = 0;
        if (mode === 'limit') {
            limit = parseInt($('#cosmeBatchLimit').val(), 10);
            if (!limit || limit < 1) {
                alert('请输入有效的匹配数量（至少 1 条）');
                return;
            }
        }
        var $btn = $('#cosmeBatchSubmitBtn');
        $btn.prop('disabled', true).text('匹配中…');
        $.post(apiUrl('/cosmeMatch/run'), {
            brand: getBrandValue(),
            onlyMissing: true,
            limit: limit
        }, function (res) {
            $btn.prop('disabled', false).text('开始匹配');
            closeCosmeBatchMatchModal();
            var msg = '处理 ' + (res.processed || 0) + ' 条：自动关联 ' + (res.autoApplied || 0) +
                '，待确认 ' + (res.pending || 0) + '，未匹配 ' + (res.notFound || 0);
            alert(msg);
            reloadList();
            pendingCosmeMatches = (res.items || []).filter(function (item) {
                return item.status === 'pending';
            });
            if (pendingCosmeMatches.length > 0) {
                openCosmeMatchModal();
            }
        }).fail(function () {
            $btn.prop('disabled', false).text('开始匹配');
            alert('Cosme 匹配请求失败');
        });
    }

    function matchCosmeOne(conditionId) {
        $.post(apiUrl('/cosmeMatch/matchOne'), { conditionId: conditionId }, function (res) {
            if (res.status === 'auto_applied') {
                alert(res.message || '已自动关联');
                reloadList();
                return;
            }
            if (res.status === 'pick' || res.status === 'pending') {
                pendingCosmeMatches = [res];
                openCosmeMatchModal();
                return;
            }
            alert(res.message || '未找到可关联的 Cosme 产品');
            reloadList();
        }).fail(function () {
            alert('Cosme 匹配请求失败');
        });
    }

    function rematchCosmeOne(conditionId) {
        $.post(apiUrl('/cosmeMatch/reset'), { conditionId: conditionId }, function () {
            matchCosmeOne(conditionId);
        }).fail(function () {
            alert('清除关联状态失败');
        });
    }

    function openCosmeMatchModal() {
        if (!pendingCosmeMatches || pendingCosmeMatches.length === 0) {
            return;
        }
        var isPickMode = pendingCosmeMatches.length === 1 && pendingCosmeMatches[0].showAllProducts;
        cosmeMatchModalMode = isPickMode ? 'pick' : 'batch';
        if (isPickMode) {
            var pickItem = pendingCosmeMatches[0];
            $('#cosmeMatchModalTitle').text('选择 Cosme 产品 · #' + pickItem.conditionId);
            $('#cosmeMatchModalFooter').html(
                '<button type="button" class="btn btn-danger" onclick="skipCosmeMatch(' + pickItem.conditionId + ')">全部不匹配</button>' +
                '<button type="button" class="btn" onclick="closeCosmeMatchModal()">关闭</button>'
            );
        } else {
            $('#cosmeMatchModalTitle').text('Cosme 匹配待确认（' + pendingCosmeMatches.length + ' 条）');
            $('#cosmeMatchModalFooter').html(
                '<button type="button" class="btn" onclick="closeCosmeMatchModal()">关闭</button>'
            );
        }
        var html = isPickMode
            ? '<div class="hint" style="margin-bottom:12px;">相似度<strong> &gt;80% </strong>已自动关联；未匹配时展示 Cosme 全部搜索结果，请<strong>挑选关联</strong>或点<strong>全部不匹配</strong>。</div>'
            : '<div class="hint" style="margin-bottom:12px;">相似度<strong> &gt;80% </strong>自动关联；<strong>≤80% </strong>展示 Cosme 首个搜索结果供确认。每条可<strong>确认关联</strong>或<strong>不进行关联</strong>。</div>';
        html += '<table class="match-table"><thead><tr>' +
            '<th>关键字</th><th>Cosme 产品</th><th>相似度</th><th>操作</th></tr></thead><tbody>';
        var skipLabel = isPickMode ? '全部不匹配' : '不进行关联';
        for (var i = 0; i < pendingCosmeMatches.length; i++) {
            var item = pendingCosmeMatches[i];
            var candidates = item.reviewCandidates && item.reviewCandidates.length
                ? item.reviewCandidates
                : (item.bestMatch ? [item.bestMatch] : []);
            var rowSpan = Math.max(candidates.length, 1);
            if (candidates.length === 0) {
                html += '<tr>' +
                    '<td>#' + item.conditionId + '<br>' + window.escapeHtml(item.description || '') +
                    '<br><small>' + window.escapeHtml(item.keyword || '') + '</small>' +
                    (isPickMode ? '' : ('<br><button type="button" class="btn btn-danger" style="margin-top:8px;" onclick="skipCosmeMatch(' +
                    item.conditionId + ')">' + skipLabel + '</button>')) +
                    '</td>' +
                    '<td colspan="3"><span class="cosme-unlinked">无候选产品</span></td></tr>';
                continue;
            }
            for (var j = 0; j < candidates.length; j++) {
                var c = candidates[j];
                var scoreClass = c.similarity > 80 ? 'match-score' : 'match-score review';
                var imgHtml = c.imageUrl
                    ? '<img src="' + window.escapeHtml(c.imageUrl) + '" referrerpolicy="no-referrer" alt="">'
                    : '';
                html += '<tr>';
                if (j === 0) {
                    html += '<td rowspan="' + rowSpan + '">#' + item.conditionId + '<br>' +
                        window.escapeHtml(item.description || '') +
                        '<br><small>' + window.escapeHtml(item.keyword || '') + '</small>';
                    if (!isPickMode) {
                        html += '<br><button type="button" class="btn btn-danger" style="margin-top:8px;" onclick="skipCosmeMatch(' +
                            item.conditionId + ')">' + skipLabel + '</button>';
                    }
                    html += '</td>';
                }
                html += '<td><div class="match-product-cell">' + imgHtml + '<div>' +
                    '<a href="' + window.escapeHtml(c.productUrl || '#') + '" target="_blank" rel="noopener">' +
                    window.escapeHtml(c.productName || '') + '</a>' +
                    '<div class="match-ref">比对：' + window.escapeHtml(c.matchedReference || '') + '</div></div></div></td>' +
                    '<td><span class="' + scoreClass + '">' + c.similarity + '%</span></td>' +
                    '<td><button type="button" class="btn btn-primary" onclick="confirmCosmeMatchByIndex(' +
                    item.conditionId + ',' + j + ')">' + (isPickMode ? '选择关联' : '确认关联') + '</button></td></tr>';
            }
        }
        html += '</tbody></table>';
        $('#cosmeMatchBody').html(html);
        $('#cosmeMatchModal').addClass('open');
    }

    function closeCosmeMatchModal(force) {
        if (!force && cosmeMatchModalMode === 'batch' && pendingCosmeMatches && pendingCosmeMatches.length > 0) {
            if (!confirm('还有 ' + pendingCosmeMatches.length + ' 条待确认未处理。\n关闭后下次批量匹配仍会弹出，请尽量确认关联或点「不进行关联」。\n\n确定关闭？')) {
                return;
            }
        }
        $('#cosmeMatchModal').removeClass('open');
        pendingCosmeMatches = [];
        cosmeMatchModalMode = 'batch';
    }

    function removePendingCosmeItem(conditionId) {
        pendingCosmeMatches = pendingCosmeMatches.filter(function (item) {
            return item.conditionId !== conditionId;
        });
        reloadList();
        if (pendingCosmeMatches.length === 0) {
            closeCosmeMatchModal(true);
        } else {
            openCosmeMatchModal();
        }
    }

    function confirmCosmeMatchByIndex(conditionId, candidateIndex) {
        var item = null;
        for (var i = 0; i < pendingCosmeMatches.length; i++) {
            if (pendingCosmeMatches[i].conditionId === conditionId) {
                item = pendingCosmeMatches[i];
                break;
            }
        }
        if (!item) {
            return;
        }
        var candidates = item.reviewCandidates && item.reviewCandidates.length
            ? item.reviewCandidates
            : (item.bestMatch ? [item.bestMatch] : []);
        var candidate = candidates[candidateIndex];
        if (!candidate) {
            return;
        }
        confirmCosmeMatch(conditionId, candidate.productId, candidate.productUrl,
            candidate.productName, candidate.imageUrl);
    }

    function confirmCosmeMatch(conditionId, productId, productUrl, productName, productImage) {
        $.ajax({
            url: apiUrl('/cosmeMatch/confirm'),
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                conditionId: conditionId,
                cosmeProductId: productId,
                cosmeProductUrl: productUrl,
                cosmeProductName: productName,
                cosmeProductImage: productImage || null
            }),
            success: function () {
                removePendingCosmeItem(conditionId);
            },
            error: function () {
                alert('确认关联失败');
            }
        });
    }

    function skipCosmeMatch(conditionId) {
        $.post(apiUrl('/cosmeMatch/skip'), { conditionId: conditionId }, function () {
            removePendingCosmeItem(conditionId);
        }).fail(function () {
            alert('操作失败');
        });
    }

    window.initPlatformCosmeMatch = function (options) {
        config = options || {};
        window.openCosmeBatchMatchModal = openCosmeBatchMatchModal;
        window.closeCosmeBatchMatchModal = closeCosmeBatchMatchModal;
        window.onCosmeBatchModeChange = onCosmeBatchModeChange;
        window.submitCosmeBatchMatch = submitCosmeBatchMatch;
        window.matchCosmeOne = matchCosmeOne;
        window.rematchCosmeOne = rematchCosmeOne;
        window.openCosmeMatchModal = openCosmeMatchModal;
        window.closeCosmeMatchModal = closeCosmeMatchModal;
        window.confirmCosmeMatchByIndex = confirmCosmeMatchByIndex;
        window.skipCosmeMatch = skipCosmeMatch;

        var extraModals = config.extraModalSelectors || '';
        var selector = '#cosmeMatchModal, #cosmeBatchMatchModal' + (extraModals ? ', ' + extraModals : '');
        $(selector).on('click', function (e) {
            if (e.target === this) {
                $(this).removeClass('open');
            }
        });
    };
})(window);
