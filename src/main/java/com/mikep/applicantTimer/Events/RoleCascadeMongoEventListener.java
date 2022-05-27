package com.mikep.applicantTimer.Events;

import com.mikep.applicantTimer.Models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

public class RoleCascadeMongoEventListener extends AbstractMongoEventListener<Role> {
    @Autowired
    private MongoOperations mongoOperations;
    private Role deletedRole;
    public @Override void onBeforeSave(BeforeSaveEvent<Role> event) {
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(),
                new CascadeSaveCallback(source, mongoOperations));
    }
    public @Override void onBeforeDelete(BeforeDeleteEvent<Role> event) {
        final Object id = Objects.requireNonNull(event.getDocument()).get("_id");
        deletedRole = mongoOperations.findById(id, Role.class);
    }
    public @Override void onAfterDelete(AfterDeleteEvent<Role> event) {
        ReflectionUtils.doWithFields(Role.class,
                new CascadeDeleteCallback(deletedRole, mongoOperations));
    }
}
