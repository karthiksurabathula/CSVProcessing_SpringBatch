package com.data.processing.batch;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.data.processing.entity.FileTracker;
import com.data.processing.util.ResourceUtil;

@Component
@StepScope
public class FileMerge implements Tasklet {

	private static final Logger logger = LoggerFactory.getLogger(FileMerge.class);

	@Value("#{jobParameters[filename]}")
	String filename;
	@Value("#{jobParameters[folder]}")
	String filePath;
	
	@Value("${batch.csv.outputFolder:data/output}")
	private String outputPath;

	@Value("${batch.csv.outputPrefix:_output}")
	private String outputPrefix;
	
	@Autowired
	private ResourceUtil resourceUtil;

	@SuppressWarnings("deprecation")
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info("File Merge process started" + filePath);

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(
				"file:" + outputPath + "/split/" + filePath + "/" + resourceUtil.getFileName(filename) + "*.split");

		File file = new File(outputPath + "/" + resourceUtil.getFileName(filename) + outputPrefix + "." 
				+ resourceUtil.getFileExtension(filename));

		for (int i = 0; i < resources.length; i++) {
				LineIterator it = FileUtils.lineIterator(new File(outputPath + "/split/" + filePath + "/" + resources[i].getFilename()), "UTF-8");
				try {
					while (it.hasNext()) {
						FileUtils.writeStringToFile(file, it.nextLine() + "\n", "UTF-8", true);
					}
				} finally {
					LineIterator.closeQuietly(it);
				}
			
		}

		
		logger.info("File Merge process completed" + filePath);
		return RepeatStatus.FINISHED;
	}
}
