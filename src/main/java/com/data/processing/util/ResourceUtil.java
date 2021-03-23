package com.data.processing.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResourceUtil {
	
	@Value("${batch.csv.inputFolder:data/input}")
	private String inputFolder;
	
	@Value("${batch.csv.outputFolder:data/output}")
	private String outputPath;
	
	public String getFileExtension(String filename) {
	    return FilenameUtils.getExtension(filename);
	}
	
	public String getFileName(String filename) {
	    return FilenameUtils.getBaseName(filename);
	}
	
	public void fileSplitter(String filename) {
		File file = new File(inputFolder + "/" + filename);
		
		
//		File file = new File(outputPath + "/" + outputFileName);
		
		
	}

}
