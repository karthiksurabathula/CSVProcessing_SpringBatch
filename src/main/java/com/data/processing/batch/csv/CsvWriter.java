package com.data.processing.batch.csv;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.data.processing.batch.csv.model.CsvDataOutput;
import com.data.processing.entity.CsvDataEntity;
import com.data.processing.repository.CsvDataRepository;
import com.data.processing.util.ResourceUtil;

@Component
@StepScope
public class CsvWriter implements ItemWriter<CsvDataOutput> {

	@Value("${batch.csv.outputFolder:data/output}")
	private String outputPath;
	@Value("${batch.csv.prefix:_output}")
	private String outputPrefix;
	 @Value("#{jobParameters[folder]}") 
	 String filePath;
	 @Value("#{jobParameters[filename]}") 
	 String inputFilename;

	@Autowired
	private CsvDataRepository csvDataRepo;
	@Autowired
	private ResourceUtil resourceUtil;

	@Override
	public void write(List<? extends CsvDataOutput> csvOutput) throws Exception {

		String outputFileName = resourceUtil.getFileName(csvOutput.get(0).getFileName()) + outputPrefix + "."
				+ resourceUtil.getFileExtension(csvOutput.get(0).getFileName());

		File file = new File(outputPath + "/split/" +filePath + "/" + outputFileName);

		for (int i = 0; i < csvOutput.size(); i++) {
			csvDataRepo.saveAndFlush(new CsvDataEntity(0, csvOutput.get(i).getInput(), csvOutput.get(i).isValid(),
					csvOutput.get(i).getSha512Checksum(), inputFilename));

			if (csvOutput.get(i).isValid()) {
				FileUtils.writeStringToFile(file, csvOutput.get(i).getInput() + "\n", "UTF-8", true);
			}

		}
	}

}
