package com.crossai.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "user_preferences")
@Data
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "preferred_input_modes", columnDefinition = "JSON")
    private Map<String, Object> preferredInputModes;
    
    @Column(name = "mode_effectiveness_scores", columnDefinition = "JSON")
    private Map<String, Double> modeEffectivenessScores;
    
    @Column(name = "learning_interactions")
    private Integer learningInteractions = 0;
    
    @Column(name = "last_mode_switch")
    private LocalDateTime lastModeSwitch;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}