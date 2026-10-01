package com.example.Project_mgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_mgmt.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);
}