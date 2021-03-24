package com.data.processing.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResourceUtil {

	private static final Logger logger = LoggerFactory.getLogger(ResourceUtil.class);

	
	
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
