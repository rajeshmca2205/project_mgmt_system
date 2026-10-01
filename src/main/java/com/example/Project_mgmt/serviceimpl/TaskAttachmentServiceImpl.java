package com.example.Project_mgmt.serviceimpl;

import com.example.Project_mgmt.dto.TaskAttachmentDto;
import com.example.Project_mgmt.entity.Task;
import com.example.Project_mgmt.entity.TaskAttachment;
import com.example.Project_mgmt.repository.TaskAttachmentRepository;
import com.example.Project_mgmt.repository.TaskRepository;
import com.example.Project_mgmt.service.TaskAttachmentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskAttachmentServiceImpl
        implements TaskAttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    private final Path uploadDirectory;

    public TaskAttachmentServiceImpl(
            TaskAttachmentRepository attachmentRepository,
            TaskRepository taskRepository,
            @Value("${file.upload-dir:uploads/tasks}") String uploadDir) {

        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;

        this.uploadDirectory =
                Paths.get(uploadDir)
                        .toAbsolutePath()
                        .normalize();

        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create upload directory",
                    e
            );
        }
    }

    @Override
    @Transactional
    public TaskAttachmentDto uploadAttachment(
            Long taskId,
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Attachment file is empty"
            );
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Task not found with id: "
                                        + taskId
                        )
                );

        /*
         * One attachment per task.
         *
         * If an attachment already exists,
         * reject the upload.
         */
        List<TaskAttachment> existingAttachments =
                attachmentRepository.findByTaskId(taskId);

        if (!existingAttachments.isEmpty()) {
            throw new RuntimeException(
                    "This task already has an attachment"
            );
        }

        String originalFileName =
                Paths.get(file.getOriginalFilename() == null
                                ? "file"
                                : file.getOriginalFilename())
                        .getFileName()
                        .toString();

        String extension = "";

        int dotIndex =
                originalFileName.lastIndexOf(".");

        if (dotIndex >= 0) {
            extension =
                    originalFileName.substring(dotIndex);
        }

        String storedFileName =
                UUID.randomUUID()
                        + extension;

        Path targetPath =
                uploadDirectory.resolve(storedFileName)
                        .normalize();

        /*
         * Prevent path traversal.
         */
        if (!targetPath.startsWith(uploadDirectory)) {
            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        try {

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not save attachment",
                    e
            );
        }

        TaskAttachment attachment =
                new TaskAttachment();

        attachment.setOriginalFileName(
                originalFileName
        );

        attachment.setStoredFileName(
                storedFileName
        );

        attachment.setFilePath(
                targetPath.toString()
        );

        attachment.setContentType(
                file.getContentType()
        );

        attachment.setFileSize(
                file.getSize()
        );

        attachment.setUploadedAt(
                LocalDateTime.now()
        );

        attachment.setTask(task);

        TaskAttachment savedAttachment =
                attachmentRepository.save(
                        attachment
                );

        return convertToDto(savedAttachment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAttachmentDto> getAttachmentsByTaskId(
            Long taskId) {

        return attachmentRepository
                .findByTaskId(taskId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskAttachmentDto getAttachmentById(
            Long attachmentId) {

        TaskAttachment attachment =
                attachmentRepository.findById(
                        attachmentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Attachment not found with id: "
                                        + attachmentId
                        )
                );

        return convertToDto(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getAttachmentFile(
            Long attachmentId) {

        TaskAttachment attachment =
                attachmentRepository.findById(
                        attachmentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Attachment not found with id: "
                                        + attachmentId
                        )
                );

        Path filePath =
                Paths.get(
                        attachment.getFilePath()
                )
                .toAbsolutePath()
                .normalize();

        if (!filePath.startsWith(uploadDirectory)) {
            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        if (!Files.exists(filePath)
                || !Files.isRegularFile(filePath)) {

            throw new RuntimeException(
                    "Attachment file not found"
            );
        }

        try {

            return new UrlResource(
                    filePath.toUri()
            );

        } catch (MalformedURLException e) {

            throw new RuntimeException(
                    "Could not load attachment",
                    e
            );
        }
    }

    @Override
    @Transactional
    public void deleteAttachment(
            Long attachmentId) {

        TaskAttachment attachment =
                attachmentRepository.findById(
                        attachmentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Attachment not found with id: "
                                        + attachmentId
                        )
                );

        Path filePath =
                Paths.get(
                        attachment.getFilePath()
                )
                .toAbsolutePath()
                .normalize();

        /*
         * Delete database record first only after
         * validating the file location.
         */
        if (!filePath.startsWith(uploadDirectory)) {
            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        try {

            Files.deleteIfExists(filePath);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not delete attachment file",
                    e
            );
        }

        attachmentRepository.delete(
                attachment
        );
    }

    private TaskAttachmentDto convertToDto(
            TaskAttachment attachment) {

        TaskAttachmentDto dto =
                new TaskAttachmentDto();

        dto.setId(
                attachment.getId()
        );

        dto.setOriginalFileName(
                attachment.getOriginalFileName()
        );

        dto.setContentType(
                attachment.getContentType()
        );

        dto.setFileSize(
                attachment.getFileSize()
        );

        dto.setUploadedAt(
                attachment.getUploadedAt()
        );

        if (attachment.getTask() != null) {
            dto.setTaskId(
                    attachment.getTask().getId()
            );
        }

        return dto;
    }
}
