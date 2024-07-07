package com.tms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Authority {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long authorityId;
	
	@Enumerated(EnumType.STRING)
	private AuthorityCode authorityCode;
	
	public enum AuthorityCode
	{
		CREATE_TASK,UPDATE_TASK,DELETE_TASK,READ_TASK
	}

	public Long getAuthorityId() {
		return authorityId;
	}

	public void setAuthorityId(Long authorityId) {
		this.authorityId = authorityId;
	}

	public AuthorityCode getAuthorityCode() {
		return authorityCode;
	}

	public void setAuthorityCode(AuthorityCode authorityCode) {
		this.authorityCode = authorityCode;
	}
	
	
}
