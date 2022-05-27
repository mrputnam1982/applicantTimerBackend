package com.mikep.applicantTimer.Repositories;

import com.mikep.applicantTimer.Models.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttachmentRepository extends MongoRepository<Attachment, String> {
    public Attachment findAttachmentByUsername(String username);
}
