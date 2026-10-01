package com.example.Project_mgmt.serviceimpl;


import org.springframework.stereotype.Service;

import com.example.Project_mgmt.dto.ProjectRequestDto;
import com.example.Project_mgmt.dto.ProjectResponseDto;
import com.example.Project_mgmt.entity.Project;
import com.example.Project_mgmt.entity.ProjectStatus;
import com.example.Project_mgmt.repository.ProjectRepository;
import com.example.Project_mgmt.service.ProjectService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(
            ProjectRepository projectRepository) {

        this.projectRepository = projectRepository;
    }

    @Override
    public List<ProjectResponseDto> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public ProjectResponseDto getProjectById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Project not found with id: " + id
                        ));

        return convertToResponseDto(project);
    }

    @Override
    public ProjectResponseDto createProject(
            ProjectRequestDto requestDto) {

        Project project = new Project();

        project.setName(requestDto.getName());
        project.setDescription(requestDto.getDescription());
        project.setStartDate(requestDto.getStartDate());
        project.setEndDate(requestDto.getEndDate());

        if (requestDto.getStatus() == null) {
            project.setStatus(ProjectStatus.PLANNED);
        } else {
            project.setStatus(requestDto.getStatus());
        }

        project.setCreatedAt(LocalDateTime.now());

        Project savedProject =
                projectRepository.save(project);

        return convertToResponseDto(savedProject);
    }

    @Override
    public ProjectResponseDto updateProject(
            Long id,
            ProjectRequestDto requestDto) {

        Project existingProject =
                projectRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found with id: " + id
                                ));

        existingProject.setName(
                requestDto.getName()
        );

        existingProject.setDescription(
                requestDto.getDescription()
        );

        existingProject.setStartDate(
                requestDto.getStartDate()
        );

        existingProject.setEndDate(
                requestDto.getEndDate()
        );

        existingProject.setStatus(
                requestDto.getStatus()
        );

        Project updatedProject =
                projectRepository.save(existingProject);

        return convertToResponseDto(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {

        if (!projectRepository.existsById(id)) {

            throw new RuntimeException(
                    "Project not found with id: " + id
            );
        }

        projectRepository.deleteById(id);
    }

    private ProjectResponseDto convertToResponseDto(
            Project project) {

        ProjectResponseDto responseDto =
                new ProjectResponseDto();

        responseDto.setId(project.getId());

        responseDto.setName(
                project.getName()
        );

        responseDto.setDescription(
                project.getDescription()
        );

        responseDto.setStartDate(
                project.getStartDate()
        );

        responseDto.setEndDate(
                project.getEndDate()
        );

        responseDto.setStatus(
                project.getStatus()
        );

        responseDto.setCreatedAt(
                project.getCreatedAt()
        );

        return responseDto;
    }
}