package com.crossai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class PlatformAdapter {
    
    /**
     * 平台适配器 - 根据不同电商平台优化内容
     */
    public Map<String, Object> adaptContentForPlatform(MultimodalContentDTO content, String platform) {
        log.info("Adapting content for platform: {}", platform);
        
        Map<String, Object> adaptedContent = new HashMap<>();
        
        switch (platform.toLowerCase()) {
            case "amazon":
                adaptedContent = adaptForAmazon(content);
                break;
            case "ebay":
                adaptedContent = adaptForEbay(content);
                break;
            case "shopify":
                adaptedContent = adaptForShopify(content);
                break;
            case "walmart":
                adaptedContent = adaptForWalmart(content);
                break;
            case "etsy":
                adaptedContent = adaptForEtsy(content);
                break;
            default:
                adaptedContent = adaptForGeneric(content, platform);
        }
        
        // 添加平台通用元数据
        adaptedContent.put("platform", platform);
        adaptedContent.put("adaptation_timestamp", System.currentTimeMillis());
        adaptedContent.put("original_content_id", content.getId());
        
        return adaptedContent;
    }
    
    /**
     * Amazon平台适配
     */
    private Map<String, Object> adaptForAmazon(MultimodalContentDTO content) {
        Map<String, Object> adapted = new HashMap<>();
        
        // 标题优化 (200字符以内)
        String title = truncateToLength(content.getGeneratedTitle(), 200);
        adapted.put("title", title);
        
        // 五点描述优化 (每点250字符以内)
        List<String> bullets = content.getGeneratedBullets();
        if (bullets != null) {
            List<String> amazonBullets = bullets.stream()
                .map(bullet -> truncateToLength(bullet, 250))
                .collect(java.util.stream.Collectors.toList());
            adapted.put("bullet_points", amazonBullets);
        }
        
        // 产品描述
        String description = content.getGeneratedDescription();
        if (description != null) {
            adapted.put("description", truncateToLength(description, 2000));
        }
        
        // 关键词优化
        List<String> keywords = content.getGeneratedKeywords();
        if (keywords != null) {
            adapted.put("keywords", String.join(", ", keywords));
        }
        
        // Amazon特有字段
        adapted.put("product_type", "physical_product");
        adapted.put("condition", "new");
        adapted.put("fulfillment_channel", "merchant");
        adapted.put("adult_product", false);
        
        return adapted;
    }
    
    /**
     * eBay平台适配
     */
    private Map<String, Object> adaptForEbay(MultimodalContentDTO content) {
        Map<String, Object> adapted = new HashMap<>();
        
        // eBay标题 (80字符以内)
        String title = truncateToLength(content.getGeneratedTitle(), 80);
        adapted.put("title", title);
        
        // eBay描述 (HTML格式)
        String description = content.getGeneratedDescription();
        if (description != null) {
            adapted.put("description", formatAsHtml(description));
        }
        
        // eBay副标题 (55字符以内)
        if (content.getGeneratedBullets() != null && !content.getGeneratedBullets().isEmpty()) {
            String subtitle = truncateToLength(content.getGeneratedBullets().get(0), 55);
            adapted.put("subtitle", subtitle);
        }
        
        // 物品属性
        adapted.put("item_specifics", generateItemSpecifics(content));
        adapted.put("condition_description", "New with tags");
        adapted.put("return_policy", "Returns Accepted");
        
        return adapted;
    }
    
    /**
     * Shopify平台适配
     */
    private Map<String, Object> adaptForShopify(MultimodalContentDTO content) {
        Map<String, Object> adapted = new HashMap<>();
        
        // Shopify标题 (通常70字符以内)
        String title = truncateToLength(content.getGeneratedTitle(), 70);
        adapted.put("title", title);
        
        // Shopify描述 (支持Markdown)
        String description = content.getGeneratedDescription();
        if (description != null) {
            adapted.put("body_html", formatAsMarkdown(description));
        }
        
        // 产品标签
        List<String> keywords = content.getGeneratedKeywords();
        if (keywords != null) {
            adapted.put("tags", String.join(", ", keywords));
        }
        
        // Shopify特有字段
        adapted.put("vendor", "CrossAI Generated");
        adapted.put("product_type", "General");
        adapted.put("published", true);
        adapted.put("inventory_management", "shopify");
        
        return adapted;
    }
    
    /**
     * Walmart平台适配
     */
    private Map<String, Object> adaptForWalmart(MultimodalContentDTO content) {
        Map<String, Object> adapted = new HashMap<>();
        
        // Walmart标题 (75字符以内)
        String title = truncateToLength(content.getGeneratedTitle(), 75);
        adapted.put("title", title);
        
        // 长描述
        String description = content.getGeneratedDescription();
        if (description != null) {
            adapted.put("long_description", description);
        }
        
        // 短描述
        if (content.getGeneratedBullets() != null && !content.getGeneratedBullets().isEmpty()) {
            String shortDesc = String.join(" | ", 
                content.getGeneratedBullets().subList(0, Math.min(2, content.getGeneratedBullets().size())));
            adapted.put("short_description", truncateToLength(shortDesc, 500));
        }
        
        // Walmart特有字段
        adapted.put("shelf_description", truncateToLength(description, 100));
        adapted.put("category_path", "Electronics > General");
        
        return adapted;
    }
    
    /**
     * Etsy平台适配
     */
    private Map<String, Object> adaptForEtsy(MultimodalContentDTO content) {
        Map<String, Object> adapted = new HashMap<>();
        
        // Etsy标题 (140字符以内)
        String title = truncateToLength(content.getGeneratedTitle(), 140);
        adapted.put("title", title);
        
        // Etsy描述 (强调手工制作和情感)
        String description = content.getGeneratedDescription();
        if (description != null) {
            String etsyDescription = "✨ Handcrafted with care ✨\n\n" + description + "\n\n🎨 Perfect for adding a personal touch to your space!";
            adapted.put("description", etsyDescription);
        }
        
        // 标签 (13个以内，每个20字符以内)
        List<String> keywords = content.getGeneratedKeywords();
        if (keywords != null) {
            List<String> etsyTags = keywords.stream()
                .map(tag -> truncateToLength(tag, 20))
                .limit(13)
                .collect(java.util.stream.Collectors.toList());
            adapted.put("tags", etsyTags);
        }
        
        return adapted;
    }
    
    /**
     * 通用平台适配
     */
    private Map<String, Object> adaptForGeneric(MultimodalContentDTO content, String platform) {
        Map<String, Object> adapted = new HashMap<>();
        adapted.put("title", content.getGeneratedTitle());
        adapted.put("description", content.getGeneratedDescription());
        adapted.put("content_type", "general");
        adapted.put("platform_note", "Generic adaptation for " + platform);
        return adapted;
    }
    
    /**
     * 生成物品属性 (eBay专用)
     */
    private Map<String, String> generateItemSpecifics(MultimodalContentDTO content) {
        Map<String, String> specifics = new HashMap<>();
        
        if (content.getGeneratedKeywords() != null) {
            specifics.put("Brand", extractBrandFromKeywords(content.getGeneratedKeywords()));
            specifics.put("Type", "Product");
        }
        
        specifics.put("Condition", "New");
        specifics.put("Country/Region of Manufacture", "China");
        
        return specifics;
    }
    
    /**
     * 从关键词提取品牌
     */
    private String extractBrandFromKeywords(List<String> keywords) {
        List<String> knownBrands = Arrays.asList("Apple", "Samsung", "Sony", "Nike", "Adidas");
        return keywords.stream()
            .filter(keyword -> knownBrands.stream().anyMatch(brand -> keyword.toLowerCase().contains(brand.toLowerCase())))
            .findFirst()
            .orElse("Generic Brand");
    }
    
    /**
     * 截断字符串到指定长度
     */
    private String truncateToLength(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * 格式化为HTML
     */
    private String formatAsHtml(String text) {
        return "<p>" + text.replace("\n\n", "</p><p>").replace("\n", "<br>") + "</p>";
    }
    
    /**
     * 格式化为Markdown
     */
    private String formatAsMarkdown(String text) {
        return text.replace("\n\n", "\n\n---\n\n");
    }
    
    /**
     * 批量平台适配
     */
    public Map<String, Map<String, Object>> adaptContentForMultiplePlatforms(
            MultimodalContentDTO content, List<String> platforms) {
        Map<String, Map<String, Object>> adaptations = new HashMap<>();
        
        for (String platform : platforms) {
            Map<String, Object> adapted = adaptContentForPlatform(content, platform);
            adaptations.put(platform, adapted);
        }
        
        return adaptations;
    }
}