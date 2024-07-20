package com.tms.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Task extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long taskId;

	@NotBlank(message = "Task Subject Cannot Blank")
	@Column(nullable = false)
	private String taskSubject;

	private String taskDescription;

	@NotNull(message = "Task Priority Cannot Be Null")
	@Column(nullable = false)
	private Short taskPriority;

	@NotNull(message = "Task Due Date Cannot Be Null")
	@Column(nullable = false)
	private LocalDateTime taskDueDate;

	@NotNull(message = "Task Status Cannot Be Empty")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TaskStatus taskStatus;

	//@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "userId")
	private TMSUser taskOwner;

	public enum TaskStatus {
		OPEN, CLOSED
	}

	public TMSUser getTaskOwner() {
		return taskOwner;
	}

	public void setTaskOwner(TMSUser taskOwner) {
		this.taskOwner = taskOwner;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskSubject() {
		return taskSubject;
	}

	public void setTaskSubject(String taskSubject) {
		this.taskSubject = taskSubject;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public Short getTaskPriority() {
		return taskPriority;
	}

	public void setTaskPriority(Short taskPriority) {
		this.taskPriority = taskPriority;
	}

	public LocalDateTime getTaskDueDate() {
		return taskDueDate;
	}

	public void setTaskDueDate(LocalDateTime taskDueDate) {
		this.taskDueDate = taskDueDate;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

}
