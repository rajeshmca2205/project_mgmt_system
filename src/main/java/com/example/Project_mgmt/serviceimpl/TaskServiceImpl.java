package com.example.Project_mgmt.serviceimpl;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Project_mgmt.dto.TaskRequestDto;
import com.example.Project_mgmt.dto.TaskResponseDto;
import com.example.Project_mgmt.entity.Project;
import com.example.Project_mgmt.entity.Task;
import com.example.Project_mgmt.entity.TaskPriority;
import com.example.Project_mgmt.entity.TaskStatus;
import com.example.Project_mgmt.repository.ProjectRepository;
import com.example.Project_mgmt.repository.TaskAttachmentRepository;
import com.example.Project_mgmt.repository.TaskRepository;
import com.example.Project_mgmt.service.TaskAttachmentService;
import com.example.Project_mgmt.service.TaskService;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskAttachmentService taskAttachmentService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            TaskAttachmentService taskAttachmentService) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.taskAttachmentService = taskAttachmentService;
    }

    @Override
    public List<TaskResponseDto> getAllTasks() {

        return taskRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Task not found with id: " + id
                        ));

        return convertToResponseDto(task);
    }

    @Override
    public List<TaskResponseDto> getTasksByProject(
            Long projectId) {

        if (!projectRepository.existsById(projectId)) {

            throw new RuntimeException(
                    "Project not found with id: " + projectId
            );
        }

        return taskRepository
                .findByProjectId(projectId)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

   /* @Override
    public TaskResponseDto createTask(
            TaskRequestDto requestDto) {

        Project project =
                projectRepository.findById(
                        requestDto.getProjectId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Project not found with id: "
                                        + requestDto.getProjectId()
                        ));

        Task task = new Task();

        task.setTitle(
                requestDto.getTitle()
        );

        task.setDescription(
                requestDto.getDescription()
        );

        task.setPriority(
                requestDto.getPriority() != null
                        ? requestDto.getPriority()
                        : TaskPriority.MEDIUM
        );

        task.setStatus(
                requestDto.getStatus() != null
                        ? requestDto.getStatus()
                        : TaskStatus.TODO
        );

        task.setDueDate(
                requestDto.getDueDate()
        );

        task.setCreatedAt(
                LocalDateTime.now()
        );

        task.setProject(project);

        Task savedTask =
                taskRepository.save(task);

        return convertToResponseDto(savedTask);
    }*/

    @Override
    @Transactional
    public TaskResponseDto createTask(
            String title,
            String description,
            String priority,
            String status,
            String dueDate,
            Long projectId,
            MultipartFile attachment) {

        Project project =
                projectRepository.findById(projectId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found with id: "
                                                + projectId
                                )
                        );

        Task task = new Task();

        task.setTitle(title);
        task.setDescription(description);

        task.setPriority(
                TaskPriority.valueOf(priority)
        );

        task.setStatus(
                TaskStatus.valueOf(status)
        );

        task.setDueDate(
                LocalDate.parse(dueDate)
        );

        task.setProject(project);

        Task savedTask =
                taskRepository.save(task);

        /*
         * Save attachment after Task has an ID.
         */
        if (attachment != null
                && !attachment.isEmpty()) {

            taskAttachmentService.uploadAttachment(
                    savedTask.getId(),
                    attachment
            );
        }

        return convertToResponseDto(savedTask);
    }
    @Override
    public TaskResponseDto updateTask(
            Long id,
            TaskRequestDto requestDto) {

        Task existingTask =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found with id: "
                                                + id
                                ));

        existingTask.setTitle(
                requestDto.getTitle()
        );

        existingTask.setDescription(
                requestDto.getDescription()
        );

        existingTask.setPriority(
                requestDto.getPriority()
        );

        existingTask.setStatus(
                requestDto.getStatus()
        );

        existingTask.setDueDate(
                requestDto.getDueDate()
        );

        if (requestDto.getProjectId() != null) {

            Project project =
                    projectRepository.findById(
                            requestDto.getProjectId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Project not found with id: "
                                            + requestDto.getProjectId()
                            ));

            existingTask.setProject(project);
        }

        Task updatedTask =
                taskRepository.save(existingTask);

        return convertToResponseDto(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {

        if (!taskRepository.existsById(id)) {

            throw new RuntimeException(
                    "Task not found with id: " + id
            );
        }

        taskRepository.deleteById(id);
    }

    private TaskResponseDto convertToResponseDto(
            Task task) {

        TaskResponseDto responseDto =
                new TaskResponseDto();

        responseDto.setId(
                task.getId()
        );

        responseDto.setTitle(
                task.getTitle()
        );

        responseDto.setDescription(
                task.getDescription()
        );

        responseDto.setPriority(
                task.getPriority()
        );

        responseDto.setStatus(
                task.getStatus()
        );

        responseDto.setDueDate(
                task.getDueDate()
        );

        responseDto.setCreatedAt(
                task.getCreatedAt()
        );

        if (task.getProject() != null) {

            responseDto.setProjectId(
                    task.getProject().getId()
            );
System.out.println("project name ...."+task.getProject().getName());
            responseDto.setProjectName(
                    task.getProject().getName()
            );
        }

        return responseDto;
    }
}