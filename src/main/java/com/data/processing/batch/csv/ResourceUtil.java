package com.data.processing.batch.csv;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

@Service
public class ResourceUtil {
	
	public String getFileExtension(String filename) {
	    return FilenameUtils.getExtension(filename);
	}
	
	public String getFileName(String filename) {
	    return FilenameUtils.getBaseName(filename);
	}

}
