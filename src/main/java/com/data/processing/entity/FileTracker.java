package com.data.processing.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.sun.istack.NotNull;
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
@SequenceGenerator(name="PRIVATE_SEQ_FILETRACKER", sequenceName="private_sequence_filetracker")
public class FileTracker {

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="PRIVATE_SEQ_FILETRACKER")
	private int id;
	@NotNull
	private String filename;
	@NotNull
	private Date tarcked_date;
	@Nullable
	private Date process_start_date;
	@Nullable
	private Date process_end_date;
	@NotNull
	private String status;
	@NotNull
	private boolean completed;
	@Nullable
	private Date lastUpdateDate;

}
