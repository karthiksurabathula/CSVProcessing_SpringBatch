package com.data.processing.batch.csv;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.data.processing.batch.csv.model.CsvDataModel;
import com.data.processing.batch.csv.model.CsvDataOutput;
import com.data.processing.batch.csv.persistance.fileTracker.FileTracker;
import com.data.processing.batch.csv.persistance.fileTracker.FileTrackerRepository;

@Configuration
public class CsvJob {

	private static final Logger logger = LoggerFactory.getLogger(CsvJob.class);

	@Value("${batch.csv.inputFolder:data/input}")
	private String inputFolder;
	@Value("${batch.csv.chunkSize:100}")
	private int chunkSize;

	@Autowired
	private FileTrackerRepository fileTrackerRepository;
	
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	ResourceLoader resourceLoader;
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	StepBuilderFactory stepBuilder;
	@Autowired
	ItemReader<CsvDataModel> itemReader;
	@Autowired
	ItemProcessor<CsvDataModel, CsvDataOutput> itemProcessor;
	@Autowired
	ItemWriter<CsvDataOutput> itemWriter;

	@Autowired
	@Qualifier("csvRestItemReader")
	ItemReader<CsvDataModel> itemReaderRest;

	/*
	 * Batch
	 
	@Bean
	@Qualifier("csvJobBatch")
	public Job createCSVJob() throws IOException {
		return jobBuilderFactory.get("csv-batch-processor-batch").incrementer(new RunIdIncrementer())
				.flow(csvProcessorStep()).end().build();
	}

	@Bean
	public Step csvProcessorStep() throws IOException {
		return stepBuilder.get("csv-file-load-and-validate-batch").<CsvDataModel, CsvDataOutput>chunk(chunkSize)
				.reader(multiResourceItemReader()).processor(itemProcessor).writer(itemWriter).build();
	}

	@Bean
	@StepScope
	public MultiResourceItemReader<CsvDataModel> multiResourceItemReader() {
		MultiResourceItemReader<CsvDataModel> resourceItemReader = new MultiResourceItemReader<CsvDataModel>();
		resourceItemReader.setResources(getResources());
		resourceItemReader.setDelegate(itemReader());
		resourceItemReader.setSaveState(true);
		return resourceItemReader;
	}

	@Bean
	@StepScope
	public FlatFileItemReader<CsvDataModel> itemReader() {
		FlatFileItemReader<CsvDataModel> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setName("csv-reader-batch");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}
	 */
	
	/*
	 * Rest
	 */
	@Bean
	@Qualifier("csvJobRest")
	public Job createCSVJobRest() throws IOException {
		return jobBuilderFactory.get("csv-batch-processor-rest").incrementer(new RunIdIncrementer())
				.flow(restCsvProcessorStep()).end().build();
	}

	@Bean
	@Qualifier("csvRestProcessor")
	public Step restCsvProcessorStep() throws IOException {
		return stepBuilder.get("csv-file-load-and-validate-rest").<CsvDataModel, CsvDataOutput>chunk(chunkSize)
				.reader(itemReaderRest).processor(itemProcessor).writer(itemWriter).faultTolerant().skipLimit(0).skip(Exception.class).build();
	}

	@Bean
	@StepScope
	@Qualifier("csvRestItemReader")
	public FlatFileItemReader<CsvDataModel> restItemReader(@Value("#{jobParameters[filename]}") String filename) {

		logger.info("Processing "+filename);
		
		FlatFileItemReader<CsvDataModel> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setName("csv-reader-rest");
		flatFileItemReader.setResource(new FileSystemResource(inputFolder + "/" + filename));
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	/*
	 * Common
	 */

	@Bean
	@StepScope
	public LineMapper<CsvDataModel> lineMapper() {

		DefaultLineMapper<CsvDataModel> defaultLineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("Customer_Id", "Transaction_Id", "Account_type", "Portfolio_Id", "Email_Address", "Date",
				"Checksum");
		defaultLineMapper.setLineTokenizer(lineTokenizer);

		BeanWrapperFieldSetMapper<CsvDataModel> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(CsvDataModel.class);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		return defaultLineMapper;
	}

	public Resource[] getResources() {

		Resource[] resources = null;
		try {
			resources = applicationContext.getResources("file:" + inputFolder + "/*.csv");
//			for (int i = 0; i < resources.length; i++) {
//				Resource a = resources[i];
//				fileTrackerRepository.saveAndFlush(new FileTracker());
//				
//			}
		} catch (IOException ex) {
			logger.error("" + ex);
		}

		return resources;
	}

}
