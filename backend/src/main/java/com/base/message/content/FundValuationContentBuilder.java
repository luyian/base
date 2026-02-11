package com.base.message.content;

import com.base.stock.fund.dto.FundValuationResponse;
import com.base.stock.fund.dto.StockQuote;
import com.base.stock.fund.service.FundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 基金估值内容生成器
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundValuationContentBuilder implements ContentBuilder {

    private final FundService fundService;

    @Override
    public String getSubType() {
        return "fund_valuation";
    }

    @Override
    public String build(Long userId) {
        List<FundValuationResponse> valuations = fundService.getAllValuationByUserId(userId);
        if (valuations == null || valuations.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📊 基金估值日报 (")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .append(")\n\n");

        for (FundValuationResponse v : valuations) {
            sb.append("【").append(v.getFundName()).append("】");
            if (v.getFundCode() != null) {
                sb.append(" (").append(v.getFundCode()).append(")");
            }
            sb.append("\n");

            if (v.getEstimatedChangePercent() != null) {
                BigDecimal change = v.getEstimatedChangePercent();
                String arrow = change.compareTo(BigDecimal.ZERO) >= 0 ? "📈" : "📉";
                String sign = change.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                sb.append(arrow).append(" 估算涨跌幅: ").append(sign).append(change).append("%\n");
            }

            if (v.getHoldingCount() != null) {
                sb.append("持仓数量: ").append(v.getHoldingCount());
                if (v.getSuccessCount() != null) {
                    sb.append(" (成功获取: ").append(v.getSuccessCount()).append(")");
                }
                sb.append("\n");
            }

            // 显示前5只持仓明细
            if (v.getQuotes() != null && !v.getQuotes().isEmpty()) {
                int count = 0;
                for (StockQuote quote : v.getQuotes()) {
                    if (count >= 5) {
                        sb.append("  ... 等共").append(v.getQuotes().size()).append("只\n");
                        break;
                    }
                    if (quote.getSuccess() && quote.getChangePercent() != null) {
                        String qSign = quote.getChangePercent().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                        sb.append("  ").append(quote.getStockName() != null ? quote.getStockName() : quote.getStockCode())
                                .append(": ").append(qSign).append(quote.getChangePercent()).append("%")
                                .append(" (权重: ").append(quote.getWeight()).append("%)\n");
                    }
                    count++;
                }
            }
            sb.append("\n");
        }

        return sb.toString().trim();
    }
}
