package com.data.processing.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "csvInput", indexes = { @Index(name = "SECONDARY_CSVData", columnList = "checksum,filename") })
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SequenceGenerator(name="PRIVATE_SEQ_CSV_DATA", sequenceName="private_sequence_csv_data")
public class CsvDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="PRIVATE_SEQ_CSV_DATA")
	private int id;
	private String input;
	private boolean valid;
	private String checksum;
	private String filename;

}
