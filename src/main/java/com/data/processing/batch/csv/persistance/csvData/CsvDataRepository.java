package com.data.processing.batch.csv.persistance.csvData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvDataRepository extends JpaRepository<CsvDataEntity, Long>{

}
