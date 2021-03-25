package com.data.processing.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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

	public void deleteFolder(String path) throws IOException {
		FileUtils.deleteDirectory(new File(path));	
	}
	
	
	
	

}
