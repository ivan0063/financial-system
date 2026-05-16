package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgCollectionCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TcgCollectionCardJpaRepository extends JpaRepository<TcgCollectionCardEntity, Long> {
    List<TcgCollectionCardEntity> findByCollection_Id(Long collectionId);

    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM TcgCollectionCardEntity c WHERE c.scryfallId = :scryfallId")
    int sumQuantityByScryfallId(@Param("scryfallId") String scryfallId);
}
