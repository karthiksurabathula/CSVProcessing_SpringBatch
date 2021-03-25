package com.data.processing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.data.processing.entity.FileTracker;

@Repository
public interface FileTrackerRepository extends JpaRepository<FileTracker, Long> {

	@Query(value = "Select f from FileTracker as f where f.filename=:filename")
	FileTracker findByFilename(@Param("filename") String filename);
	
	@Query(value = "Select f from FileTracker as f where f.completed=:status")
	List<FileTracker> findProcessedFiles(@Param("status") boolean status);
	
}
