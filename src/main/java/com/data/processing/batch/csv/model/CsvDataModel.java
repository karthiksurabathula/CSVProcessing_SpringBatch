package com.data.processing.batch.csv.model;

import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CsvDataModel implements ResourceAware {
	
	private Resource resource;
	
	private String customerId;
	private String transactionId;
	private String accountType;
	private String portfolioId;
	private String emailAddress;
	private String date;
	private String checksum;

}
