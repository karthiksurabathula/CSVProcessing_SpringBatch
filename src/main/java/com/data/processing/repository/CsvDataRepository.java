package com.data.processing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.data.processing.entity.CsvDataEntity;

@Repository
public interface CsvDataRepository extends JpaRepository<CsvDataEntity, Long>{

}
