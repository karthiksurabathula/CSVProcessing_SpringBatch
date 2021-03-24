package com.data.processing.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class JobService {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	@Qualifier("csvJobRestSingleThreaded")
	Job job;

	@Autowired
	@Qualifier("csvJobRestMultiThreaded")
	Job csvJobMulti;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	public JobExecution csvJob(String filename) throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		Date date = new Date();
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addDate("date", new Date());
		jobParametersBuilder.addString("filename", filename);
		jobParametersBuilder.addString("folder", sdf.format(date).toString());
		JobExecution jobExecution = jobLauncher.run(csvJobMulti, jobParametersBuilder.toJobParameters());
		return jobExecution;
	}

}
