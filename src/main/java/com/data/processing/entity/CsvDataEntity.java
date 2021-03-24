package com.data.processing.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "csvInput")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CsvDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String input;
	private boolean valid;
	private String checksum;
	private String fileName;

}
