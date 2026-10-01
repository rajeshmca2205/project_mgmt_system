package com.example.Project_mgmt.service;


import java.util.List;

import com.example.Project_mgmt.dto.ProjectRequestDto;
import com.example.Project_mgmt.dto.ProjectResponseDto;

public interface ProjectService {

    List<ProjectResponseDto> getAllProjects();

    ProjectResponseDto getProjectById(Long id);

    ProjectResponseDto createProject(
            ProjectRequestDto requestDto
    );

    ProjectResponseDto updateProject(
            Long id,
            ProjectRequestDto requestDto
    );

    void deleteProject(Long id);
}
