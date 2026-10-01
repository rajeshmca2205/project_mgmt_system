package com.example.Project_mgmt.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Project_mgmt.dto.ProjectRequestDto;
import com.example.Project_mgmt.dto.ProjectResponseDto;
import com.example.Project_mgmt.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(
            ProjectService projectService) {

        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>>
    getAllProjects() {

        return ResponseEntity.ok(
                projectService.getAllProjects()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto>
    getProjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                projectService.getProjectById(id)
        );
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto>
    createProject(
            @RequestBody ProjectRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        projectService.createProject(requestDto)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto>
    updateProject(
            @PathVariable Long id,
            @RequestBody ProjectRequestDto requestDto) {

        return ResponseEntity.ok(
                projectService.updateProject(
                        id,
                        requestDto
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteProject(
            @PathVariable Long id) {

        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }
}