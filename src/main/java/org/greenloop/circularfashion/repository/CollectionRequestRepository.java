package org.greenloop.circularfashion.repository;

import org.greenloop.circularfashion.entity.CollectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CollectionRequestRepository extends JpaRepository<CollectionRequest, UUID> {
}









