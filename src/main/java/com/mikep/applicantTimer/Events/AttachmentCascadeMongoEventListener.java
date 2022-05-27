package com.mikep.applicantTimer.Events;


import com.mikep.applicantTimer.Models.Attachment;
import com.mikep.applicantTimer.Models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;


public class AttachmentCascadeMongoEventListener extends AbstractMongoEventListener<Attachment> {


    @Autowired
    private MongoOperations mongoOperations;
    private Attachment deletedAttachment;
    public @Override void onBeforeSave(BeforeSaveEvent<Attachment> event) {
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(),
                new CascadeSaveCallback(source, mongoOperations));
    }
    public @Override void onBeforeDelete(BeforeDeleteEvent<Attachment> event) {
        final Object id = Objects.requireNonNull(event.getDocument()).get("_id");
        deletedAttachment = mongoOperations.findById(id, Attachment.class);
    }
    public @Override void onAfterDelete(AfterDeleteEvent<Attachment> event) {
        ReflectionUtils.doWithFields(Attachment.class,
                new CascadeDeleteCallback(deletedAttachment, mongoOperations));
    }
}