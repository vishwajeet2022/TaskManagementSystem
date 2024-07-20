package com.tms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tms.model.Task.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tms_user")
public class TMSUser extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;

	@NotBlank(message = "Name Cannot Be Blank")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Email Cannot Be Blank")
	@Email(message = "Not a Well Formatted Email adresss")
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_authorties_map", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId"), inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "authorityId"))
	private List<Authority> authorities = new ArrayList<>();

	//@JsonManagedReference
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "taskOwner")
	private List<Task> tasks = new ArrayList<>();

	
	@Column(nullable = false)
	private String password;

	public enum Role {
		ROLE_USER, ROLE_ADMIN
	}

	public void addTask(Task task) {
		if (tasks == null)
			tasks = new ArrayList<>();
		tasks.add(task);
	}

	@JsonIgnore
	public List<Task> getOpenTasks() {
		List<Task> openTasks = null;
		for (Task task : this.getTasks()) {
			if (task.getTaskStatus().equals(TaskStatus.OPEN)) {
				if (openTasks == null)
					openTasks = new ArrayList<>();
				openTasks.add(task);
			}
		}
		return openTasks;
	}

	@JsonIgnore
	public List<Task> getOpenTasksOnDueDate(LocalDateTime time) {
		List<Task> openTaksOnDueDate = null;
		for (Task task : this.getOpenTasks()) {
			if (task.getTaskDueDate().toLocalDate().equals(time.toLocalDate())) {
				if (openTaksOnDueDate == null)
					openTaksOnDueDate = new ArrayList<>();
				openTaksOnDueDate.add(task);
			}
		}
		return openTaksOnDueDate;

	}

	public Long getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	};

}
