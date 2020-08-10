package com.msf.dbbase.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "user")
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;
	@Column(name = "birthday")
	private Date birthday;
}
