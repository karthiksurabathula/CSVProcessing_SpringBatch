package com.data.processing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.data.processing.entity.FileTracker;

@Repository
public interface FileTrackerRepository extends JpaRepository<FileTracker, Long>{

}
