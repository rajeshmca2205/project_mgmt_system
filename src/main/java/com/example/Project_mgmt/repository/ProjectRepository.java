package com.example.Project_mgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_mgmt.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
