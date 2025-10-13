package org.greenloop.circularfashion.repository;

import org.greenloop.circularfashion.entity.PointEarningRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PointEarningRuleRepository extends JpaRepository<PointEarningRule, UUID> {

    Optional<PointEarningRule> findByRuleName(String ruleName);
    
    @Query("SELECT r FROM PointEarningRule r WHERE r.isActive = true ORDER BY r.createdAt DESC")
    Optional<PointEarningRule> findActiveRule();
    
    boolean existsByRuleName(String ruleName);
}









