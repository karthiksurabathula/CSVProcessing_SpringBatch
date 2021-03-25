package com.data.processing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.data.processing.entity.OperationCheckEntity;

@Repository
public interface OperationCheckRepository extends JpaRepository<OperationCheckEntity, String>{

}
