package com.example.backend.promotion;

import java.util.List;
import java.util.Optional;   // <<<<<< ต้องมีอันนี้

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
        select p from Promotion p
        where (:q is null or :q = '' or
               lower(p.name) like lower(concat('%', :q, '%'))
               or lower(p.code) like lower(concat('%', :q, '%')))
          and (:status is null or p.status = :status)
        order by p.priority asc, p.id asc
        """)
    List<Promotion> search(
            @Param("q") String q,
            @Param("status") PromotionStatus status
    );

    // >>> อันนี้คือเมธอดที่ PromotionService เรียกใช้อยู่
    Optional<Promotion> findFirstByCodeIgnoreCase(String code);
}
