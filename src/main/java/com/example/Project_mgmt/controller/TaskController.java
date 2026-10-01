package com.example.Project_mgmt.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.MediaType;
import com.example.Project_mgmt.dto.TaskRequestDto;
import com.example.Project_mgmt.dto.TaskResponseDto;
import com.example.Project_mgmt.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>>
    getAllTasks() {

        return ResponseEntity.ok(
                taskService.getAllTasks()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto>
    getTaskById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                taskService.getTaskById(id)
        );
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponseDto>>
    getTasksByProject(
            @PathVariable Long projectId) {
System.out.println("projectId.............."+projectId);
        return ResponseEntity.ok(
                taskService.getTasksByProject(projectId)
        );
    }

   /* @PostMapping
    public ResponseEntity<TaskResponseDto>
    createTask(
            @RequestBody TaskRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        taskService.createTask(requestDto)
                );
    }*/
    
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<TaskResponseDto> createTask(

            @RequestParam("title")
            String title,

            @RequestParam("description")
            String description,

            @RequestParam("priority")
            String priority,

            @RequestParam("status")
            String status,

            @RequestParam("dueDate")
            String dueDate,

            @RequestParam("projectId")
            Long projectId,

            @RequestParam(
                    value = "attachment",
                    required = false
            )
            MultipartFile attachment) {

        TaskResponseDto response =
                taskService.createTask(
                        title,
                        description,
                        priority,
                        status,
                        dueDate,
                        projectId,
                        attachment
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto>
    updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequestDto requestDto) {

        return ResponseEntity.ok(
                taskService.updateTask(
                        id,
                        requestDto
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }
}