package com.example.Project_mgmt.service;


import com.example.Project_mgmt.dto.TaskAttachmentDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskAttachmentService {

    TaskAttachmentDto uploadAttachment(
            Long taskId,
            MultipartFile file
    );

    List<TaskAttachmentDto> getAttachmentsByTaskId(
            Long taskId
    );

    TaskAttachmentDto getAttachmentById(
            Long attachmentId
    );

    Resource getAttachmentFile(
            Long attachmentId
    );

    void deleteAttachment(
            Long attachmentId
    );
}