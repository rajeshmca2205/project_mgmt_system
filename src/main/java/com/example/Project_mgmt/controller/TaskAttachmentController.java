package com.example.Project_mgmt.controller;

import com.example.Project_mgmt.dto.TaskAttachmentDto;
import com.example.Project_mgmt.service.TaskAttachmentService;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/task-attachments")
public class TaskAttachmentController {

    private final TaskAttachmentService attachmentService;

    public TaskAttachmentController(
            TaskAttachmentService attachmentService) {

        this.attachmentService =
                attachmentService;
    }

    /*
     * Upload attachment
     */
    @PostMapping(
            value = "/task/{taskId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<TaskAttachmentDto> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) {

        TaskAttachmentDto response =
                attachmentService.uploadAttachment(
                        taskId,
                        file
                );

        return ResponseEntity.ok(response);
    }

    /*
     * Get attachment metadata
     */
    @GetMapping("/{attachmentId}")
    public ResponseEntity<TaskAttachmentDto> getAttachment(
            @PathVariable Long attachmentId) {

        return ResponseEntity.ok(
                attachmentService.getAttachmentById(
                        attachmentId
                )
        );
    }

    /*
     * Get all attachments for a task
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskAttachmentDto>>
    getTaskAttachments(
            @PathVariable Long taskId) {

        return ResponseEntity.ok(
                attachmentService
                        .getAttachmentsByTaskId(taskId)
        );
    }

    /*
     * View attachment in browser
     */
    @GetMapping("/{attachmentId}/view")
    public ResponseEntity<Resource> viewAttachment(
            @PathVariable Long attachmentId) {

        TaskAttachmentDto attachment =
                attachmentService.getAttachmentById(
                        attachmentId
                );

        Resource resource =
                attachmentService.getAttachmentFile(
                        attachmentId
                );

        MediaType mediaType =
                MediaType.APPLICATION_OCTET_STREAM;

        if (attachment.getContentType() != null
                && !attachment.getContentType().isBlank()) {

            try {

                mediaType =
                        MediaType.parseMediaType(
                                attachment.getContentType()
                        );

            } catch (Exception ignored) {

                mediaType =
                        MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(
                                        attachment
                                                .getOriginalFileName()
                                )
                                .build()
                                .toString()
                )
                .body(resource);
    }

    /*
     * Download attachment
     */
    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long attachmentId) {

        TaskAttachmentDto attachment =
                attachmentService.getAttachmentById(
                        attachmentId
                );

        Resource resource =
                attachmentService.getAttachmentFile(
                        attachmentId
                );

        MediaType mediaType =
                MediaType.APPLICATION_OCTET_STREAM;

        if (attachment.getContentType() != null
                && !attachment.getContentType().isBlank()) {

            try {

                mediaType =
                        MediaType.parseMediaType(
                                attachment.getContentType()
                        );

            } catch (Exception ignored) {

                mediaType =
                        MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        attachment
                                                .getOriginalFileName()
                                )
                                .build()
                                .toString()
                )
                .body(resource);
    }

    /*
     * Delete attachment
     */
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<String> deleteAttachment(
            @PathVariable Long attachmentId) {

        attachmentService.deleteAttachment(
                attachmentId
        );

        return ResponseEntity.ok(
                "Attachment deleted successfully"
        );
    }
}
