package com.crossai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdaptiveLearningSystem {
    
    // 内存中的学习模型
    private final Map<Long, UserLearningProfile> userProfiles = new ConcurrentHashMap<>();
    private final Map<String, PatternLearningModel> patternModels = new ConcurrentHashMap<>();
    
    public Map<String, Object> learnFromInteraction(Long userId, 
                                                  Map<String, Object> interactionData, 
                                                  Map<String, Object> outcomeData) {
        log.info("Learning from interaction for user: {}", userId);
        
        // 1. 更新用户学习档案
        UserLearningProfile profile = getUserLearningProfile(userId);
        updateUserProfile(profile, interactionData, outcomeData);
        
        // 2. 识别成功模式
        PatternLearningModel patternModel = getPatternLearningModel(interactionData);
        PatternRecognitionResult patternResult = recognizeSuccessPatterns(patternModel, interactionData, outcomeData);
        
        // 3. 生成个性化建议
        PersonalizedRecommendations recommendations = generatePersonalizedRecommendations(profile, patternResult);
        
        Map<String, Object> learningResult = new HashMap<>();
        learningResult.put("user_profile_update", profile);
        learningResult.put("pattern_recognition", patternResult);
        learningResult.put("personalized_recommendations", recommendations);
        learningResult.put("learning_metadata", Map.of(
            "learning_timestamp", System.currentTimeMillis(),
            "interaction_type", interactionData.get("type")
        ));
        
        return learningResult;
    }
    
    private UserLearningProfile getUserLearningProfile(Long userId) {
        return userProfiles.computeIfAbsent(userId, k -> {
            UserLearningProfile newProfile = new UserLearningProfile();
            newProfile.setUserId(userId);
            newProfile.setLearningInteractions(0);
            newProfile.setPreferredModes(new HashMap<>());
            return newProfile;
        });
    }
    
    private void updateUserProfile(UserLearningProfile profile, 
                                Map<String, Object> interactionData, 
                                Map<String, Object> outcomeData) {
        profile.setLearningInteractions(profile.getLearningInteractions() + 1);
        
        String usedMode = (String) interactionData.get("input_mode");
        if (usedMode != null) {
            profile.getPreferredModes().put(usedMode, 
                profile.getPreferredModes().getOrDefault(usedMode, 0) + 1);
        }
        
        profile.setLastLearningTime(System.currentTimeMillis());
    }
    
    private PatternLearningModel getPatternLearningModel(Map<String, Object> interactionData) {
        String modelKey = (String) interactionData.getOrDefault("domain", "general");
        return patternModels.computeIfAbsent(modelKey, k -> new PatternLearningModel(modelKey));
    }
    
    private PatternRecognitionResult recognizeSuccessPatterns(PatternLearningModel model, 
                                                          Map<String, Object> interactionData, 
                                                          Map<String, Object> outcomeData) {
        PatternRecognitionResult result = new PatternRecognitionResult();
        result.setPredictedSuccessProbability(0.8);
        result.setKeySuccessFactors(Arrays.asList("高质量输入", "清晰意图"));
        return result;
    }
    
    private PersonalizedRecommendations generatePersonalizedRecommendations(UserLearningProfile profile, 
                                                                          PatternRecognitionResult patternResult) {
        PersonalizedRecommendations recommendations = new PersonalizedRecommendations();
        recommendations.setRecommendedInputMode("hybrid_intelligence");
        return recommendations;
    }
    
    // 内部数据类
    public static class UserLearningProfile {
        private Long userId;
        private Integer learningInteractions;
        private Map<String, Integer> preferredModes;
        private Long lastLearningTime;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getLearningInteractions() { return learningInteractions; }
        public void setLearningInteractions(Integer learningInteractions) { this.learningInteractions = learningInteractions; }
        public Map<String, Integer> getPreferredModes() { return preferredModes; }
        public void setPreferredModes(Map<String, Integer> preferredModes) { this.preferredModes = preferredModes; }
        public Long getLastLearningTime() { return lastLearningTime; }
        public void setLastLearningTime(Long lastLearningTime) { this.lastLearningTime = lastLearningTime; }
    }
    
    public static class PatternLearningModel {
        private String domain;
        public PatternLearningModel(String domain) { this.domain = domain; }
        public String getDomain() { return domain; }
    }
    
    public static class PatternRecognitionResult {
        private Double predictedSuccessProbability;
        private List<String> keySuccessFactors;
        
        public Double getPredictedSuccessProbability() { return predictedSuccessProbability; }
        public void setPredictedSuccessProbability(Double predictedSuccessProbability) { this.predictedSuccessProbability = predictedSuccessProbability; }
        public List<String> getKeySuccessFactors() { return keySuccessFactors; }
        public void setKeySuccessFactors(List<String> keySuccessFactors) { this.keySuccessFactors = keySuccessFactors; }
    }
    
    public static class PersonalizedRecommendations {
        private String recommendedInputMode;
        public String getRecommendedInputMode() { return recommendedInputMode; }
        public void setRecommendedInputMode(String recommendedInputMode) { this.recommendedInputMode = recommendedInputMode; }
    }
}