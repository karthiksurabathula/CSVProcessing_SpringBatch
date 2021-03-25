package com.data.processing.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.data.processing.entity.FileTracker;
import com.data.processing.repository.FileTrackerRepository;
import com.data.processing.service.JobService;
import com.data.processing.util.ResourceUtil;

@Component
public class InputFolderWatcher {

	@Value("${batch.csv.inputFolder:data/input}")
	private String inputFolder;

	@Autowired
	private ResourceUtil resourceUtil;
	@Autowired
	private JobService csvJobService;
	@Autowired
	private FileTrackerRepository fileTrackerRepo;

	private static final Logger logger = LoggerFactory.getLogger(InputFolderWatcher.class);

	private static WatchService watchService;
	private static Path path;

	@PostConstruct
	public void init() {
		logger.info("Monitor Input Folder for New CSV files");
		try {
			watchService = FileSystems.getDefault().newWatchService();
			
			//Check and create Input directory if dosenot exist
			File dir = new File(inputFolder);
			if (!dir.exists())
				dir.mkdirs();

			path = Paths.get(new FileSystemResource(inputFolder).getPath());

			path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
		} catch (NoSuchFileException fe) {
			logger.error("Input Folder not found", fe);
		} catch (Exception e1) {
			logger.error("", e1);
		}

		/**
		 * Start monitoring thread
		 */
		Thread watchThread = new Thread(new WatcherThread());
		watchThread.setDaemon(true);
		watchThread.start();

		/**
		 * registered shutdown hook
		 */
		Thread hook = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					watchService.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(hook);

	}

	public class WatcherThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					// try to obtain monitoring changes in the pool, if not then been waiting for
					WatchKey watchKey = watchService.take();
					for (WatchEvent<?> event : watchKey.pollEvents()) {
						if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
							String editFileName = event.context().toString();
							if (resourceUtil.getFileExtension(editFileName).equals("csv")) {
								logger.info("New CSV file detected : " + path + "/" + editFileName);
								
								FileTracker fileTracker = fileTrackerRepo.findByFilename(editFileName); 
								
								if(fileTracker==null) {
									fileTrackerRepo.saveAndFlush(
											new FileTracker(0, editFileName, new Date(), null, null, "tracked", false, new Date()));
									csvJobService.csvJob(editFileName);	
								} else {
									logger.info("Job not executed, File processed on :" + fileTracker.getProcess_end_date());
								}
								
							}
						}
					}
					watchKey.reset();
				} catch (ClosedWatchServiceException e) {
					continue;
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}
	}

}
