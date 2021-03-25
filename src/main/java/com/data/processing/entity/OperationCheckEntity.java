package com.data.processing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operationcheck")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OperationCheckEntity {
	
	@Id
	private String jobType;
	private boolean status;

}
