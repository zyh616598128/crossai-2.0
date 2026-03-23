package com.crossai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class A_BTestManager {
    
    private final Map<String, Map<String, Object>> activeTests = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> testResults = new ConcurrentHashMap<>();
    
    /**
     * A/B测试管理器 - 管理内容变体测试和优胜者推广
     */
    public Map<String, Object> createABTest(String contentId, List<Map<String, Object>> variants, 
                                          Map<String, Object> testConfig) {
        log.info("Creating A/B test for content: {}", contentId);
        
        String testId = "test_" + System.currentTimeMillis() + "_" + contentId;
        
        Map<String, Object> test = new HashMap<>();
        test.put("test_id", testId);
        test.put("content_id", contentId);
        test.put("variants", variants);
        test.put("test_config", testConfig);
        test.put("status", "running");
        test.put("start_time", System.currentTimeMillis());
        test.put("traffic_split", testConfig.getOrDefault("traffic_split", 
            Collections.nCopies(variants.size(), 1.0 / variants.size())));
        test.put("success_metrics", testConfig.getOrDefault("success_metrics", 
            List.of("click_through_rate", "conversion_rate", "engagement_time")));
        
        activeTests.put(testId, test);
        
        return Map.of(
            "test_id", testId,
            "status", "created",
            "variant_count", variants.size(),
            "estimated_duration", testConfig.getOrDefault("duration_days", 7)
        );
    }
    
    /**
     * 记录测试结果
     */
    public void recordTestResult(String testId, String variantId, Map<String, Object> metrics) {
        if (!activeTests.containsKey(testId)) {
            log.warn("Test not found: {}", testId);
            return;
        }
        
        Map<String, Object> test = activeTests.get(testId);
        
        // 记录变体表现
        String resultKey = testId + "_" + variantId;
        Map<String, Object> variantResults = testResults.getOrDefault(resultKey, new HashMap<>());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> currentMetrics = (Map<String, Object>) variantResults.getOrDefault("metrics", new HashMap<>());
        
        // 更新指标
        metrics.forEach((key, value) -> {
            double current = currentMetrics.containsKey(key) ? 
                Double.parseDouble(currentMetrics.get(key).toString()) : 0.0;
            double increment = Double.parseDouble(value.toString());
            currentMetrics.put(key, current + increment);
        });
        
        variantResults.put("metrics", currentMetrics);
        variantResults.put("impression_count", 
            ((Number) variantResults.getOrDefault("impression_count", 0)).intValue() + 1);
        variantResults.put("last_updated", System.currentTimeMillis());
        
        testResults.put(resultKey, variantResults);
    }
    
    /**
     * 分析测试结果并确定优胜者
     */
    public Map<String, Object> analyzeTestResults(String testId) {
        if (!activeTests.containsKey(testId)) {
            return Map.of("error", "Test not found");
        }
        
        Map<String, Object> test = activeTests.get(testId);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> variants = (List<Map<String, Object>>) test.get("variants");
        
        // 计算每个变体的综合得分
        Map<String, Double> variantScores = new HashMap<>();
        Map<String, Map<String, Object>> variantDetails = new HashMap<>();
        
        for (Map<String, Object> variant : variants) {
            String variantId = (String) variant.get("id");
            String resultKey = testId + "_" + variantId;
            
            if (testResults.containsKey(resultKey)) {
                Map<String, Object> results = testResults.get(resultKey);
                double score = calculateVariantScore(results);
                variantScores.put(variantId, score);
                variantDetails.put(variantId, results);
            } else {
                variantScores.put(variantId, 0.0);
            }
        }
        
        // 确定优胜者
        String winnerId = variantScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        Map<String, Object> analysis = Map.of(
            "test_id", testId,
            "winner_id", winnerId,
            "variant_scores", variantScores,
            "variant_details", variantDetails,
            "statistical_significance", calculateStatisticalSignificance(variantScores),
            "recommendation", winnerId != null ? "promote_winner" : "continue_test"
        );
        
        // 更新测试状态
        if (winnerId != null && shouldConcludeTest(test)) {
            concludeTest(testId, winnerId);
        }
        
        return analysis;
    }
    
    /**
     * 计算变体得分
     */
    private double calculateVariantScore(Map<String, Object> results) {
        @SuppressWarnings("unchecked")
        Map<String, Object> metrics = (Map<String, Object>) results.get("metrics");
        int impressions = (Integer) results.get("impression_count");
        
        if (impressions == 0) return 0.0;
        
        // 加权综合得分 (CTR权重0.4, 转化率权重0.4, 参与度权重0.2)
        double ctr = metrics.containsKey("click_through_rate") ? 
            Double.parseDouble(metrics.get("click_through_rate").toString()) : 0.0;
        double conversion = metrics.containsKey("conversion_rate") ? 
            Double.parseDouble(metrics.get("conversion_rate").toString()) : 0.0;
        double engagement = metrics.containsKey("engagement_time") ? 
            Double.parseDouble(metrics.get("engagement_time").toString()) / 100.0 : 0.0;
        
        return (ctr * 0.4) + (conversion * 0.4) + (engagement * 0.2);
    }
    
    /**
     * 计算统计显著性
     */
    private double calculateStatisticalSignificance(Map<String, Double> scores) {
        if (scores.size() < 2) return 1.0;
        
        double maxScore = Collections.max(scores.values());
        double avgScore = scores.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        return maxScore > avgScore ? (maxScore - avgScore) / avgScore : 0.0;
    }
    
    /**
     * 判断是否应该结束测试
     */
    private boolean shouldConcludeTest(Map<String, Object> test) {
        long startTime = (Long) test.get("start_time");
        long currentTime = System.currentTimeMillis();
        int durationDays = (Integer) test.getOrDefault("duration_days", 7);
        
        return (currentTime - startTime) > (durationDays * 24 * 60 * 60 * 1000L);
    }
    
    /**
     * 结束测试
     */
    private void concludeTest(String testId, String winnerId) {
        Map<String, Object> test = activeTests.get(testId);
        test.put("status", "completed");
        test.put("winner_id", winnerId);
        test.put("end_time", System.currentTimeMillis());
        
        log.info("A/B test concluded: {} -> winner: {}", testId, winnerId);
    }
    
    /**
     * 获取活跃测试列表
     */
    public List<Map<String, Object>> getActiveTests() {
        return activeTests.entrySet().stream()
            .map(entry -> {
                Map<String, Object> test = new HashMap<>(entry.getValue());
                test.put("test_id", entry.getKey());
                return test;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 停止测试
     */
    public Map<String, Object> stopTest(String testId) {
        if (!activeTests.containsKey(testId)) {
            return Map.of("error", "Test not found");
        }
        
        Map<String, Object> test = activeTests.get(testId);
        test.put("status", "stopped");
        test.put("end_time", System.currentTimeMillis());
        
        return Map.of("test_id", testId, "status", "stopped");
    }
}