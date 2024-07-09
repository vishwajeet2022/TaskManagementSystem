package com.tms.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.exception.BusinessException;
import com.tms.exception.ErrorModel;
import com.tms.exception.IErrorMessageConstants;
import com.tms.model.Authority;
import com.tms.model.TMSUser;
import com.tms.model.Task;
import com.tms.repository.IAuthorityRepository;
import com.tms.repository.ITaskRepository;
import com.tms.repository.IUserRepository;

@Service
public class TMSApplicationService {

	@Autowired
	IAuthorityRepository authorityRepository;
	@Autowired
	IUserRepository userRepository;
	@Autowired
	ITaskRepository taskRepository;

	@Transactional
	public Task createTask(Task taskTo) {
		taskTo.setTaskId(null);
		validateTaskOwner(taskTo);
		return taskRepository.save(taskTo);
	}

	private void validateTaskOwner(Task taskTo) {
		List<ErrorModel> errors = new ArrayList<>();
		if (taskTo.getTaskOwner() != null && taskTo.getTaskOwner().getUserId() != null) {
			TMSUser userDetails = getUser(taskTo.getTaskOwner().getUserId());
			if (userDetails != null)
				taskTo.setTaskOwner(userDetails);
			else
				errors.add(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.USER_NOT_FOUND));
		}
		if (!errors.isEmpty())
			throw new BusinessException(errors);
	}

	@Transactional
	public void deleteTask(Long taskId) {

		Task task = getTask(taskId);
		List<ErrorModel> errors = new ArrayList<>();
		if (task != null) {
			taskRepository.delete(task);
		} else {
			errors.add(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.TASK_NOT_FOUND));
		}
		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

	}

	public Task getTask(Long taskId) {
		Task task = null;
		Optional<Task> optionalTask = taskRepository.findById(taskId);
		if (optionalTask.isPresent()) {
			task = optionalTask.get();
		}
		return task;
	}

	public List<Task> getAllTask() {
		return taskRepository.findAll();
	}

	@Transactional
	public Task updateTaskDetails(Long taskId, Task taskInputTo) {
		Task taskTOUpdate = getTask(taskId);
		if (taskTOUpdate != null)
			updateTaskFields(taskInputTo, taskTOUpdate);
		else
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.TASK_NOT_FOUND)));
		return taskRepository.save(taskTOUpdate);
	}

	private void updateTaskFields(Task taskInputTo, Task taskTOUpdate) {
		List<ErrorModel> errors = new ArrayList<>();
		boolean isChanged = false;
		if (taskTOUpdate != null) {
			if ((taskTOUpdate == null && taskInputTo != null) || (taskTOUpdate != null && taskInputTo == null)
					|| (!taskTOUpdate.getTaskDescription().equals(taskInputTo.getTaskDescription()))) {
				taskTOUpdate.setTaskDescription(taskInputTo.getTaskDescription());
				isChanged = true;
			}
			if (!taskTOUpdate.getTaskSubject().equals(taskInputTo.getTaskSubject())) {
				taskTOUpdate.setTaskSubject(taskInputTo.getTaskSubject());
				isChanged = true;
			}
			if (taskTOUpdate.getTaskDueDate().compareTo(taskInputTo.getTaskDueDate()) != 0) {
				taskTOUpdate.setTaskDueDate(taskInputTo.getTaskDueDate());
				isChanged = true;
			}
			if (taskTOUpdate.getTaskPriority() != taskInputTo.getTaskPriority()) {
				taskTOUpdate.setTaskPriority(taskInputTo.getTaskPriority());
				isChanged = true;
			}
			if (!taskTOUpdate.getTaskStatus().equals(taskInputTo.getTaskStatus())) {
				taskTOUpdate.setTaskStatus(taskInputTo.getTaskStatus());
				isChanged = true;
			}
		} else {
			errors.add(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.TASK_NOT_FOUND));
		}
		if (isChanged == false)
			errors.add(new ErrorModel(HttpStatus.BAD_REQUEST.value(), IErrorMessageConstants.NO_CHANGE_REQUESTED));
		if (!errors.isEmpty())
			throw new BusinessException(errors);
	}

	/************ USER FUNCTIONALTIES **************/

	public List<TMSUser> getAllUsers() {
		return userRepository.findAll();

	}

	public TMSUser getUser(Long userId) {
		TMSUser tmsUser = null;
		Optional<TMSUser> optionalUser = userRepository.findById(userId);
		if (optionalUser.isPresent()) {
			tmsUser = optionalUser.get();
		}
		return tmsUser;
	}

	@Transactional
	public TMSUser createUser(TMSUser user) {
		validateUserEmail(user);
		validateUserAuthorities(user);
		//user.setUserId(null);
		 userRepository.save(user);
		return user;
	}

	private void validateUserAuthorities(TMSUser user) {
		List<Authority> authorities = authorityRepository.findAll();
		user.setAuthorities(authorities);
	}

	@Transactional
	public TMSUser updateUser(Long userId, TMSUser userInput) {
		TMSUser userToBeUpdated = this.getUser(userId);
		if (userToBeUpdated != null)
			validateUserUpdates(userInput, userToBeUpdated);
		else
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.USER_NOT_FOUND)));
		userInput = userRepository.save(userToBeUpdated);
		return userInput;
	}

	private void validateUserEmail(TMSUser user) {
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.CONFLICT.value(), IErrorMessageConstants.EMAIL_ALREADY_EXIST)));
		}
	}

	private void validateUserUpdates(TMSUser userInput, TMSUser userToBeUpdated) {
		boolean isChanged = false;
		List<ErrorModel> errors = new ArrayList<>();
		if (userToBeUpdated != null) {
			if (!userToBeUpdated.getEmail().equals(userInput.getEmail())) {
				isChanged = true;
				validateUserEmail(userInput);
				userToBeUpdated.setEmail(userInput.getEmail());
			}
			if (isChanged == false) {
				errors.add(new ErrorModel(HttpStatus.BAD_REQUEST.value(), IErrorMessageConstants.NO_CHANGE_REQUESTED));
			}
		}
		if (!errors.isEmpty())
			throw new BusinessException(errors);
	}

	@Transactional
	public void deleteUser(Long userId) {

		TMSUser user = this.getUser(userId);
		if(user!=null)
			userRepository.delete(user);
		else
			throw new BusinessException(List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.USER_NOT_FOUND)));
	}

}
