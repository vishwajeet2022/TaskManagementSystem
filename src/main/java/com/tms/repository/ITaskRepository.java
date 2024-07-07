package com.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.model.Task;

@Repository
public interface ITaskRepository extends JpaRepository<Task, Long> {

}
