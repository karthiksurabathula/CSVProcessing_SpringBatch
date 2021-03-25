package com.data.processing.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.data.processing.batch.csv.CsvJob;
import com.data.processing.entity.FileTracker;
import com.data.processing.entity.OperationCheckEntity;
import com.data.processing.repository.FileTrackerRepository;
import com.data.processing.repository.OperationCheckRepository;

@Service
@EnableAsync
public class JobService {

	private static final Logger logger = LoggerFactory.getLogger(JobService.class);
	
	@Autowired
	JobLauncher jobLauncher;
	@Autowired
	CsvJob csvJob;

	@Autowired
	@Qualifier("csvJobRestSingleThreaded")
	Job job;

	@Autowired
	@Qualifier("csvJobRestMultiThreaded")
	Job csvJobMulti;
	
	@Autowired	
	@Qualifier("csvUniqeExtracterJob")
	Job csvUniqueExtractorJob;
	
	@Autowired
	private FileTrackerRepository fileTrackerRepo;
	@Autowired
	private OperationCheckRepository operationCheckRepo; 

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Async
	public JobExecution csvJob(String filename) throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		Date date = new Date();
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addDate("date", new Date());
		jobParametersBuilder.addString("filename", filename);
		jobParametersBuilder.addString("folder", sdf.format(date).toString()+((int) (Math.random()*1000)));
		JobExecution jobExecution = jobLauncher.run(csvJobMulti, jobParametersBuilder.toJobParameters());
		return jobExecution;
	}

	@Async
	public void checkUniqueVlauesFromInput() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		
		Optional<OperationCheckEntity> check = operationCheckRepo.findById("uniqueDataExtractor");
		if(!check.isPresent()) {
			operationCheckRepo.saveAndFlush(new OperationCheckEntity("uniqueDataExtractor",false));
			check = operationCheckRepo.findById("uniqueDataExtractor");
		}
		
		if(!check.get().isStatus()) {
			List<FileTracker> files = fileTrackerRepo.findProcessedFiles(true);
			if(files.size()>1) {
				String filesToCompare = "";
				for(int i=0;i<files.size();i++) {
					if(i==files.size()-1) {
						filesToCompare = filesToCompare + files.get(i).getFilename();
					}else {
						filesToCompare = filesToCompare + files.get(i).getFilename() + ",";	
					}
				}
				logger.info("files to Extract unique" + filesToCompare);
				
				JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
				jobParametersBuilder.addDate("date", new Date());
				jobParametersBuilder.addString("files", filesToCompare);
				
				jobLauncher.run(csvUniqueExtractorJob, jobParametersBuilder.toJobParameters());
			}
		}
		
		
	}
}
