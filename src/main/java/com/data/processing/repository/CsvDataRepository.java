package com.data.processing.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.data.processing.entity.CsvDataEntity;

@Repository
public interface CsvDataRepository extends JpaRepository<CsvDataEntity, Long>{

//	@Query(value = "select * from CSV_INPUT where (checksum in (SELECT checksum FROM CSV_INPUT GROUP BY checksum  HAVING COUNT(*)=1) ) and  filename=:filename", nativeQuery = true)
//	List<CsvDataEntity> findUniqueRecordsByFile(@Param("filename") String filename, Pageable pageable);
	

	@Query(value = "select * from CSV_INPUT where (checksum in (SELECT  checksum FROM CSV_INPUT where filename in :files GROUP BY checksum  HAVING COUNT(*)=1) ) and filename=:filename ", nativeQuery = true)
	List<CsvDataEntity> findUniqueRecordsByFile(@Param("files") List<String> files,@Param("filename") String filename, Pageable pageable);
	
}
