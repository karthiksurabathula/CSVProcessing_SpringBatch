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
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.data.processing.batch.csv.model.CsvDataModel;
import com.data.processing.batch.csv.model.CsvDataOutput;

@Configuration
public class CsvJob {

	private static final Logger logger = LoggerFactory.getLogger(CsvJob.class);

	@Value("${batch.csv.inputFolder:data/input}")
	private String inputFolder;
	@Value("${batch.csv.chunkSize:100}")
	private int chunkSize;

	@Autowired
	JobBuilderFactory jobBuilderFactory;
	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	ResourceLoader resourceLoader;

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
	 * Rest
	 */
	@Bean
	@Qualifier("csvJobRestSingleThreaded")
	public Job createCSVJobRest(Step myStep) throws IOException {
		return jobBuilderFactory.get("csv-batch-processor-rest-single").validator(null)
				.incrementer(new RunIdIncrementer()).flow(restCsvProcessorStep()).end().build();
	}

	@Bean
	@Qualifier("csvRestProcessor")
	public Step restCsvProcessorStep() throws IOException {
		return stepBuilder.get("csv-file-load-and-validate-rest").<CsvDataModel, CsvDataOutput>chunk(chunkSize)
				.reader(itemReaderRest).processor(itemProcessor).writer(itemWriter).build();
	}

	@Bean
	@StepScope
	@Qualifier("csvRestItemReader")
	public FlatFileItemReader<CsvDataModel> restItemReader(@Value("#{jobParameters[filename]}") String filename) {
		logger.info("Processing " + filename);
		FlatFileItemReader<CsvDataModel> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setName("csv-reader-rest");
		flatFileItemReader.setResource(new FileSystemResource(inputFolder + "/" + filename));
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

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

}
