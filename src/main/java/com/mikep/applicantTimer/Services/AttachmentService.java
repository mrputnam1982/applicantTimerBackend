package com.mikep.applicantTimer.Services;

import com.mikep.applicantTimer.Models.Attachment;
import com.mikep.applicantTimer.Repositories.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService {
    @Autowired
    AttachmentRepository attachmentRepository;
    public Attachment updateAttachment(MultipartFile file, String username) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try{
            if(fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence " +
                        fileName);
            }

            Attachment attachment = attachmentRepository.findAttachmentByUsername(username);

            if(attachment == null)
            {
                attachment = new Attachment(
                        username,
                        fileName,
                        file.getContentType(),
                        file.getBytes());
            }
            else {
                attachment.setFileName(fileName);
                attachment.setFileType(file.getContentType());
                attachment.setFileData(file.getBytes());
            }


            return attachmentRepository.save(attachment);

        } catch(Exception e) {
            return null;
        }

    }
}
