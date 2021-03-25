package com.data.processing.batch.csv;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.data.processing.entity.CsvDataEntity;
import com.data.processing.repository.CsvDataRepository;
import com.data.processing.util.ResourceUtil;

@Component
@StepScope
public class CsvUniqeWriter implements Tasklet {

	private static final Logger logger = LoggerFactory.getLogger(CsvUniqeWriter.class);
	
	@Value("${batch.csv.outputFolder:data/output}")
	private String outputPath;
	@Value("${batch.csv.prefix:_output}")
	private String outputPrefix;
	@Value("#{jobParameters[files]}")
	String inputFiles;

	@Autowired
	private CsvDataRepository csvDataRepo;
	@Autowired
	private ResourceUtil resourceUtil;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		//Loop through files
		
		String[] filesString = inputFiles.split(",");
		List<String> files = Arrays.asList(filesString);
		
		logger.info("Input Files to extract unique data:" + inputFiles);
		
		for(int j=0;j<files.size();j++) {
			
			logger.info("Extracting unique data for :" + files.get(j));
			
			int page = 0;
			while (true) {
				Pageable blockPaging = PageRequest.of(page, 100);
				List<CsvDataEntity> res = csvDataRepo.findUniqueRecordsByFile(files, files.get(j), blockPaging);
				logger.debug("page: " + page + "  size" + res.size());
				if (res.size() == 0) {
					break;
				}
				
				String outputFileName = resourceUtil.getFileName(files.get(j)) + outputPrefix + "_unique" + "."
						+ resourceUtil.getFileExtension(files.get(j));
				File file = new File(outputPath + "/unique/" + "/" + outputFileName);
				for (int i = 0; i < res.size(); i++) {
					FileUtils.writeStringToFile(file, res.get(i).getInput() + "\n", "UTF-8", true);
				}
				page = page + 1;
			}	
		}
		
		return RepeatStatus.FINISHED;
	}
}
