package com.data.processing.batch.csv;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.data.processing.batch.csv.model.CsvDataModel;

public class CsvMultiResourceItemReader implements ItemReader<CsvDataModel>  {

	@Override
	public CsvDataModel read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
