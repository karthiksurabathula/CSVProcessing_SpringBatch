package com.data.processing.batch.csv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CsvDataOutput {
	
	private String input;
	private boolean valid;
	private String sha512Checksum;
	private String fileName;

}
