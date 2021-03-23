package com.data.processing.batch.csv.persistance.fileTracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTrackerRepository extends JpaRepository<FileTracker, Long>{

}
