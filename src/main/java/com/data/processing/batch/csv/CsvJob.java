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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;

import com.data.processing.batch.FileCleanUp;
import com.data.processing.batch.FileMerge;
import com.data.processing.batch.FileSplitter;
import com.data.processing.batch.JobExecListeners;
import com.data.processing.batch.csv.model.CsvDataModel;
import com.data.processing.batch.csv.model.CsvDataOutput;
import com.data.processing.util.ResourceUtil;

@Configuration
public class CsvJob {

	private static final Logger logger = LoggerFactory.getLogger(CsvJob.class);

	@Autowired
	private ResourceUtil resourceUtil;

	@Value("${batch.csv.inputFolder:data/input}")
	private String inputFolder;
	@Value("${batch.csv.chunkSize:100}")
	private int chunkSize;
	@Value("${batch.csv.threadsMultiple:4}")
	private int threadMultiple;

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

	@Autowired
	@Qualifier("partitioner")
	Partitioner partitioner;
	@Autowired
	@Qualifier("csvRestItemReaderMulti")
	ItemReader<CsvDataModel> itemReaderRestMulti;
	@Autowired
	FileSplitter fileSplitter;
	@Autowired
	FileCleanUp fileCleanup;
	@Autowired
	FileMerge fileMerge;
	@Autowired
	CsvUniqeWriter csvUniquWriter;
	@Autowired
	CsvUniqueCleanup csvUniqueCleanup;

	@Autowired
	@Qualifier("taskExecutor")
	TaskExecutor taskExecutor;
	
	@Autowired
	JobExecListeners jobExcListner;

	/*
	 * Rest Multithreaded
	 */
	@Bean
	@Qualifier("csvJobRestMultiThreaded")
	public Job createCSVJobRestMulti() throws IOException {
		return jobBuilderFactory.get("csv-batch-processor-rest-multi").incrementer(new RunIdIncrementer()).listener(jobExcListner)
				.flow(FileSplitter()).next(masterStep()).next(FileMerge()).next(FileCleanup()).end().build();
	}

	@Bean("partitioner")
	@StepScope
	public Partitioner partitioner(@Value("#{jobParameters[filename]}") String filename,
			@Value("#{jobParameters[folder]}") String filePath) throws IOException {
		logger.info("In Partitioner");
		MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;
		resources = resolver.getResources(
				"file:" + inputFolder + "/split/" + filePath + "/" + resourceUtil.getFileName(filename) + "*.split");
		partitioner.setResources(resources);
		partitioner.partition(Runtime.getRuntime().availableProcessors()*threadMultiple);
		return partitioner;
	}

	@Bean
	@Qualifier("masterStep")
	public Step masterStep() throws IOException {
		return stepBuilderFactory.get("csv-batch-master").partitioner("step1", partitioner)
				.step(restCsvProcessorStepMulti()).taskExecutor(taskExecutor).build();
	}

	@Bean
	@Qualifier("fileSplitterStep")
	protected Step FileSplitter() {
		return stepBuilder.get("fileSplitter").tasklet(fileSplitter).build();
	}
	
	@Bean
	@Qualifier("fileMerge")
	protected Step FileMerge() {
		return stepBuilder.get("fileMerge").tasklet(fileMerge).build();
	}

	@Bean
	@Qualifier("fileCleanup")
	protected Step FileCleanup() {
		return stepBuilder.get("fileCleanup").tasklet(fileCleanup).build();
	}

	@Bean
	@Qualifier("csvRestProcessorMulti")
	public Step restCsvProcessorStepMulti() throws IOException {
		return stepBuilder.get("csv-file-load-and-validate-rest").<CsvDataModel, CsvDataOutput>chunk(chunkSize)
				.reader(itemReaderRestMulti).processor(itemProcessor).writer(itemWriter).build();
	}

	@Bean
	@StepScope
	@Qualifier("csvRestItemReaderMulti")
	public FlatFileItemReader<CsvDataModel> restItemReaderMulti(
			@Value("#{stepExecutionContext['fileName']}") String filename) {

		logger.info("Processing Multi" + filename);
		FlatFileItemReader<CsvDataModel> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setName("csv-reader-rest-multi");
		flatFileItemReader.setResource(new PathMatchingResourcePatternResolver().getResource(filename));
//		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setSaveState(false);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;

	}

	/*
	 * Rest Single Threaded
	 */
	@Bean
	@Qualifier("csvJobRestSingleThreaded")
	public Job createCSVJobRest(Step myStep) throws IOException {
		return jobBuilderFactory.get("csv-batch-processor-rest-single").validator(null)
				.incrementer(new RunIdIncrementer()).flow(restCsvProcessorStep()).end().build();
	}

	@Primary
	@Bean
	@Qualifier("csvRestProcessor")
	public Step restCsvProcessorStep() throws IOException {
		return stepBuilder.get("csv-file-load-and-validate-rest").<CsvDataModel, CsvDataOutput>chunk(chunkSize)
				.reader(itemReaderRest).processor(itemProcessor).writer(itemWriter).build();
	}

	@Primary
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
	
	/*
	 * CSV Uniq Extractor
	 */
	@Bean
	@Qualifier("csvUniqeExtracterJob")
	public Job csvUniqeExtracterJob() throws IOException {
		logger.info("Uniqe extactor invoked");
		return jobBuilderFactory.get("csv-unique-extractor").incrementer(new RunIdIncrementer())
				.flow(csvUniqueCleanUp())
				.next(csvUniqueExtracter())
				.end().build();
	}
	
	@Bean
	@Qualifier("csvUniqueCleanup")
	public Step csvUniqueCleanUp() {
		return stepBuilder.get("csv-unique-cleanup")
				.tasklet(csvUniqueCleanup)
				.build();
	}
	
	@Bean
	@Qualifier("csvUniqueExtracter")
	public Step csvUniqueExtracter() {
		return stepBuilder.get("csv-unique-extractor")
				.tasklet(csvUniquWriter)
				.build();
	}

}
