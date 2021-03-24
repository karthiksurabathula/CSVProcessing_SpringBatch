package com.data.processing.batch;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
import org.springframework.stereotype.Component;

import com.data.processing.util.ResourceUtil;

@Component
@StepScope
public class FileSplitter implements Tasklet {

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
	
	@SuppressWarnings("deprecation")
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		logger.info("file Split Process started for: " + filename);

		Path path = Paths.get(inputFolder + "/" + filename);
		try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {

			// partitions
			long[] partitions = split(stream.count(), Runtime.getRuntime().availableProcessors() * 4);

			// reader
			LineIterator it = FileUtils.lineIterator(new File(inputFolder + "/" + filename), "UTF-8");
			try {
				int filecount = 0;
				long recordsCount = 0;
				// write
				String outputFileName = resourceUtil.getFileName(filename) + filecount + ".split";
				File file = new File(inputFolder + "/split/" + filePath + "/" + outputFileName);

				while (it.hasNext()) {
					if (recordsCount == partitions[filecount]) {
						filecount = filecount + 1;
						recordsCount = 0;
						outputFileName = resourceUtil.getFileName(filename) + filecount + ".split";
						file = new File(inputFolder + "/split/" + filePath + "/" + outputFileName);
					}
					FileUtils.writeStringToFile(file, it.nextLine() + "\n", "UTF-8", true);
					recordsCount = recordsCount + 1;
				}
			} finally {
				LineIterator.closeQuietly(it);
			}
		}
		logger.info("file Split Process completed for: " + filename);

		return RepeatStatus.FINISHED;
	}


	public long[] split(long lineCount, int n) {
		
		logger.debug("lines :"+lineCount);
	
		long partitionSize[] = new long[n];
		int x = 0;
		if (lineCount < n)
			System.out.print("-1 ");
		else if (lineCount % n == 0) {
			for (long i = 0; i < n; i++) {
				partitionSize[x] = (lineCount / n);
				x += 1;
			}
		} else {
			long zp = n - (lineCount % n);
			long pp = lineCount / n;
			for (int i = 0; i < n; i++) {
				if (i >= zp) {
					partitionSize[x] = (pp + 1);
					x += 1;
				} else {
					partitionSize[x] = pp;
					x += 1;
				}
			}
		}
		for (int i = 0; i < partitionSize.length; i++) {
			logger.debug("Partiition Size : " + i + ": " + partitionSize[i]);
		}

		return partitionSize;
	}
}
