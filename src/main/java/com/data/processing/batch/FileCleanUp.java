package com.data.processing.batch;

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
public class FileCleanUp  implements Tasklet{
	
	private static final Logger logger = LoggerFactory.getLogger(FileSplitter.class);

	@Value("#{jobParameters[filename]}")
	String filename;
	@Value("#{jobParameters[folder]}")
	String filePath;

	@Value("${batch.csv.inputFolder:data/input}")
	private String inputFolder;

	@Value("${batch.csv.outputFolder:data/output}")
	private String outputPath;

	@Autowired
	private ResourceUtil resourceUtil;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info("clean up task started for job folder : " + filePath);
		resourceUtil.deleteFolder(inputFolder + "/split/" + filePath);
		resourceUtil.deleteFolder(outputPath + "/split/" +filePath );
		logger.info("clean up task completed for job folder : " + filePath);
		return RepeatStatus.FINISHED;
	}

}
