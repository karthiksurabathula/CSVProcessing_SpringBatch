package com.data.processing.batch;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.data.processing.entity.FileTracker;
import com.data.processing.repository.FileTrackerRepository;
import com.data.processing.service.JobService;

@Component
public class JobExecListeners implements JobExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(FileSplitter.class);

	@Autowired
	private FileTrackerRepository fileTrackerRepo;
	@Autowired
	private JobService jobSvc;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		String filename = jobExecution.getJobParameters().getString("filename");
		logger.info("Job started :" + filename);
		FileTracker fileTracker = fileTrackerRepo.findByFilename(filename);
		fileTracker.setProcess_start_date(new Date());
		fileTracker.setStatus("Job started");
		fileTracker.setLastUpdateDate(new Date());
		fileTrackerRepo.saveAndFlush(fileTracker);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		String filename = jobExecution.getJobParameters().getString("filename");

		logger.info("Job Ended :" + filename);
		FileTracker fileTracker = fileTrackerRepo.findByFilename(filename);
		fileTracker.setProcess_end_date(new Date());
		fileTracker.setStatus("Job Ended");
		fileTracker.setLastUpdateDate(new Date());
		logger.info(""+jobExecution.getExitStatus().getExitCode().toString());
		if (jobExecution.getExitStatus().getExitCode().equals("COMPLETED")) {
			fileTracker.setCompleted(true);
		}
		fileTrackerRepo.saveAndFlush(fileTracker);
		
		try {
			jobSvc.checkUniqueVlauesFromInput();
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			logger.error("",e);
		}
		
		
		
	}

}
