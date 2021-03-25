package com.data.processing.batch.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.data.processing.util.ResourceUtil;

@Component
@StepScope
public class CsvUniqueCleanup implements Tasklet {

	private static final Logger logger = LoggerFactory.getLogger(CsvUniqueCleanup.class);

	@Value("${batch.csv.outputFolder:data/output}")
	private String outputPath;
	@Autowired
	private ResourceUtil resourceUtil;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info("delete Unique data output folder : " + outputPath + "/unique");
		resourceUtil.deleteFolder(outputPath + "/unique");
		return RepeatStatus.FINISHED;
	}

}
