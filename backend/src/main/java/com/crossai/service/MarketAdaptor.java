package com.crossai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class MarketAdaptor {
    
    /**
     * 市场适配器 - 文化本地化和季节性调整
     */
    public Map<String, Object> adaptToMarket(Map<String, Object> content, 
                                           String targetMarket, 
                                           Map<String, Object> adaptationParams) {
        log.info("Adapting content for market: {}", targetMarket);
        
        Map<String, Object> adaptedContent = new HashMap<>(content);
        
        // 1. 文化本地化
        Map<String, Object> culturalAdaptation = adaptCulturally(content, targetMarket);
        adaptedContent.putAll(culturalAdaptation);
        
        // 2. 季节性调整
        if (adaptationParams.containsKey("seasonal_adjustment") && 
            (Boolean) adaptationParams.get("seasonal_adjustment")) {
            Map<String, Object> seasonalAdaptation = adaptSeasonally(content, targetMarket);
            adaptedContent.putAll(seasonalAdaptation);
        }
        
        // 3. 地区偏好调整
        Map<String, Object> regionalAdaptation = adaptRegionally(content, targetMarket);
        adaptedContent.putAll(regionalAdaptation);
        
        // 4. 价格策略调整
        if (content.containsKey("price")) {
            Map<String, Object> pricingAdaptation = adaptPricing(content, targetMarket);
            adaptedContent.putAll(pricingAdaptation);
        }
        
        adaptedContent.put("market_adaptation", Map.of(
            "target_market", targetMarket,
            "adaptation_timestamp", System.currentTimeMillis(),
            "adaptation_type", "comprehensive"
        ));
        
        return adaptedContent;
    }
    
    /**
     * 文化本地化适配
     */
    private Map<String, Object> adaptCulturally(Map<String, Object> content, String market) {
        Map<String, Object> adaptation = new HashMap<>();
        
        switch (market.toLowerCase()) {
            case "china":
                adaptation.putAll(adaptForChineseMarket(content));
                break;
            case "usa":
                adaptation.putAll(adaptForUSMarket(content));
                break;
            case "europe":
                adaptation.putAll(adaptForEuropeanMarket(content));
                break;
            case "japan":
                adaptation.putAll(adaptForJapaneseMarket(content));
                break;
            default:
                adaptation.put("cultural_note", "Standard international adaptation");
        }
        
        return adaptation;
    }
    
    private Map<String, Object> adaptForChineseMarket(Map<String, Object> content) {
        Map<String, Object> adaptation = new HashMap<>();
        
        // 标题优化 - 添加中文友好的词汇
        if (content.containsKey("title")) {
            String title = (String) content.get("title");
            adaptation.put("localized_title", title + " - 优选精品");
        }
        
        // 描述优化 - 强调品质和服务
        if (content.containsKey("description")) {
            String desc = (String) content.get("description");
            adaptation.put("localized_description", desc + "\n\n✨ 正品保证 ✨ 7天无理由退换 ✨ 全国联保");
        }
        
        // 关键词优化 - 添加中文热搜词
        List<String> chineseKeywords = Arrays.asList("正品", "优惠", "包邮", "品质", "服务");
        adaptation.put("chinese_keywords", chineseKeywords);
        
        return adaptation;
    }
    
    private Map<String, Object> adaptForUSMarket(Map<String, Object> content) {
        Map<String, Object> adaptation = new HashMap<>();
        
        // 强调个人价值和便利性
        if (content.containsKey("description")) {
            String desc = (String) content.get("description");
            adaptation.put("localized_description", desc + "\n\n🇺🇸 Premium Quality • Fast Shipping • Easy Returns");
        }
        
        // 添加美式英语表达
        adaptation.put("warranty_note", "30-day money-back guarantee");
        adaptation.put("shipping_note", "Free shipping on orders over $25");
        
        return adaptation;
    }
    
    private Map<String, Object> adaptForEuropeanMarket(Map<String, Object> content) {
        Map<String, Object> adaptation = new HashMap<>();
        
        // 强调环保和CE认证
        if (content.containsKey("description")) {
            String desc = (String) content.get("description");
            adaptation.put("localized_description", desc + "\n\n🌍 Eco-friendly • CE Certified • GDPR Compliant");
        }
        
        adaptation.put("environmental_note", "Carbon-neutral shipping available");
        return adaptation;
    }
    
    private Map<String, Object> adaptForJapaneseMarket(Map<String, Object> content) {
        Map<String, Object> adaptation = new HashMap<>();
        
        // 强调精致和细节
        if (content.containsKey("title")) {
            String title = (String) content.get("title");
            adaptation.put("localized_title", "【限定】" + title);
        }
        
        if (content.containsKey("description")) {
            String desc = (String) content.get("description");
            adaptation.put("localized_description", desc + "\n\n🎌 こだわりの品質 • 丁寧な包装 • 安心のアフターサービス");
        }
        
        return adaptation;
    }
    
    /**
     * 季节性调整
     */
    private Map<String, Object> adaptSeasonally(Map<String, Object> content, String market) {
        Map<String, Object> adaptation = new HashMap<>();
        
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1; // 1-12
        
        if (month >= 11 || month <= 2) {
            // 冬季
            adaptation.put("seasonal_theme", "winter");
            adaptation.put("seasonal_keywords", Arrays.asList("冬季", "保暖", "节日", "新年"));
        } else if (month >= 3 && month <= 5) {
            // 春季
            adaptation.put("seasonal_theme", "spring");
            adaptation.put("seasonal_keywords", Arrays.asList("春季", "清新", "新品", "花园"));
        } else if (month >= 6 && month <= 8) {
            // 夏季
            adaptation.put("seasonal_theme", "summer");
            adaptation.put("seasonal_keywords", Arrays.asList("夏季", "清凉", "户外", "旅行"));
        } else {
            // 秋季
            adaptation.put("seasonal_theme", "autumn");
            adaptation.put("seasonal_keywords", Arrays.asList("秋季", "收获", "温暖", "舒适"));
        }
        
        return adaptation;
    }
    
    /**
     * 地区偏好调整
     */
    private Map<String, Object> adaptRegionally(Map<String, Object> content, String market) {
        Map<String, Object> adaptation = new HashMap<>();
        
        switch (market.toLowerCase()) {
            case "china":
                adaptation.put("payment_preferences", Arrays.asList("支付宝", "微信支付", "银联"));
                adaptation.put("shipping_preference", "快递到付");
                break;
            case "usa":
                adaptation.put("payment_preferences", Arrays.asList("Credit Card", "PayPal", "Apple Pay"));
                adaptation.put("shipping_preference", "Free shipping");
                break;
            case "europe":
                adaptation.put("payment_preferences", Arrays.asList("SEPA", "PayPal", "Klarna"));
                adaptation.put("shipping_preference", "Express delivery");
                break;
            default:
                adaptation.put("payment_preferences", Arrays.asList("Credit Card", "PayPal"));
        }
        
        return adaptation;
    }
    
    /**
     * 价格策略调整
     */
    private Map<String, Object> adaptPricing(Map<String, Object> content, String market) {
        Map<String, Object> adaptation = new HashMap<>();
        
        Double originalPrice = Double.parseDouble(content.get("price").toString());
        Double adjustedPrice = originalPrice;
        String pricingStrategy = "standard";
        
        switch (market.toLowerCase()) {
            case "china":
                // 中国市场偏好整数定价
                adjustedPrice = Math.floor(originalPrice / 10) * 10.0;
                pricingStrategy = "rounded_chinese";
                break;
            case "usa":
                // 美国市场常用.99定价
                adjustedPrice = Math.floor(originalPrice) + 0.99;
                pricingStrategy = "psychological_us";
                break;
            case "europe":
                // 欧洲市场考虑VAT
                adjustedPrice = originalPrice * 1.2; // 假设20% VAT
                pricingStrategy = "vat_included";
                break;
            default:
                adjustedPrice = originalPrice;
        }
        
        adaptation.put("original_price", originalPrice);
        adaptation.put("adjusted_price", adjustedPrice);
        adaptation.put("pricing_strategy", pricingStrategy);
        adaptation.put("currency", getCurrencyForMarket(market));
        
        return adaptation;
    }
    
    private String getCurrencyForMarket(String market) {
        Map<String, String> currencies = Map.of(
            "china", "CNY",
            "usa", "USD",
            "europe", "EUR",
            "japan", "JPY"
        );
        return currencies.getOrDefault(market.toLowerCase(), "USD");
    }
    
    /**
     * 批量市场适配
     */
    public List<Map<String, Object>> adaptBatchToMarket(List<Map<String, Object>> contents, 
                                                      String targetMarket) {
        return contents.stream()
                .map(content -> adaptToMarket(content, targetMarket, new HashMap<>()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取市场适配统计
     */
    public Map<String, Object> getMarketAdaptationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("adaptor_version", "3D-2-v1.0");
        stats.put("supported_markets", Arrays.asList("china", "usa", "europe", "japan"));
        stats.put("adaptation_types", Arrays.asList("cultural", "seasonal", "regional", "pricing"));
        stats.put("avg_adaptation_time_ms", 800);
        return stats;
    }
}