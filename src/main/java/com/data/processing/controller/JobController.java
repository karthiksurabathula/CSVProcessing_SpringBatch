package com.data.processing.controller;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.data.processing.service.JobService;

//@RestController
public class JobController {
	
  @Autowired
  private JobService jobsvc;
    
    @GetMapping(path = "/load")
    public BatchStatus loadFile(@RequestParam String filename) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
    	return jobsvc.csvJob(filename).getStatus();
    }
    
    @GetMapping(path = "/csvUnique")
    public String createUnique() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException  {
    	jobsvc.checkUniqueVlauesFromInput();
    	return "Invoked";
    }
    
	

}
