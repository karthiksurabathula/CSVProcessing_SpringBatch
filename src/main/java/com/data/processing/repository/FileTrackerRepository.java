package com.data.processing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.data.processing.entity.FileTracker;

@Repository
public interface FileTrackerRepository extends JpaRepository<FileTracker, Long> {
//
//	@Query(value = "SELECT input FROM CSV_INPUT GROUP BY checksum  HAVING COUNT(*)=1", nativeQuery = true)
//	List<SchoolEntity> getBySchoolByCityid(@Param("cityCode") long cityCode);
}
