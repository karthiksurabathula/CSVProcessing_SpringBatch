package com.data.processing.batch.csv.persistance.fileTracker;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sun.istack.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fileTracker")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FileTracker {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String filename;
	private String tarcked_date;
	@Nullable
	private Date process_start_date;
	@Nullable
	private Date process_end_date;
	private String status;
	private boolean completed;

}
