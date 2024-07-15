package com.tms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tms.Service.TMSApplicationService;
import com.tms.exception.BusinessException;
import com.tms.exception.ErrorModel;
import com.tms.exception.IErrorMessageConstants;
import com.tms.model.Task;

import jakarta.validation.Valid;

@RestController
public class TaskController {
	@Autowired
	TMSApplicationService tmsApplicationService;

	@GetMapping("/tasks")
	public ResponseEntity<?> getAllTasks() {
		List<Task> allTasks = tmsApplicationService.getAllTasks();
		if (allTasks != null && !allTasks.isEmpty())
			return new ResponseEntity<>(allTasks, HttpStatus.FOUND);
		else
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.TASK_NOT_FOUND)));
	}

	@GetMapping("/tasks/{taskId}")
	public ResponseEntity<?> getTask(@PathVariable(required = true) Long taskId) {
		Task task = tmsApplicationService.getTask(taskId);
		if (task != null && task.getTaskId() > 0)
			return new ResponseEntity<>(task, HttpStatus.FOUND);
		else
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.TASK_NOT_FOUND)));
	}

	@PostMapping("/tasks")
	public ResponseEntity<?> createTask(@Valid @RequestBody(required = true) Task task) {
		ResponseEntity<Object> response = null;
		tmsApplicationService.createTask(task);
		response = new ResponseEntity<>(task, HttpStatus.CREATED);
		return response;
	}

	@PutMapping("/tasks/{taskId}")
	public ResponseEntity<?> updateTask(@PathVariable(required = true) Long taskId,
			@Valid @RequestBody(required = true) Task task) {
		Task updatedTask = tmsApplicationService.updateTaskDetails(taskId, task);
		return new ResponseEntity<>(updatedTask, HttpStatus.OK);
	}

	@DeleteMapping("/tasks/{taskId}")
	public ResponseEntity<?> deleteTask(@PathVariable(required = true) Long taskId) {
		tmsApplicationService.deleteTask(taskId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
