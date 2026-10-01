package com.example.Project_mgmt.service;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.Project_mgmt.dto.TaskRequestDto;
import com.example.Project_mgmt.dto.TaskResponseDto;

public interface TaskService {

    List<TaskResponseDto> getAllTasks();

    TaskResponseDto getTaskById(Long id);

    List<TaskResponseDto> getTasksByProject(
            Long projectId
    );

   /* TaskResponseDto createTask(
            TaskRequestDto requestDto
    );*/
    
    TaskResponseDto createTask(
            String title,
            String description,
            String priority,
            String status,
            String dueDate,
            Long projectId,
            MultipartFile attachment
    );

    TaskResponseDto updateTask(
            Long id,
            TaskRequestDto requestDto
    );

    void deleteTask(Long id);
}
