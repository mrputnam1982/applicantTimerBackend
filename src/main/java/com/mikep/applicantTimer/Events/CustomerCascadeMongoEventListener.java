package com.mikep.applicantTimer.Events;

import com.mikep.applicantTimer.Models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

public class CustomerCascadeMongoEventListener extends AbstractMongoEventListener<Customer> {
    @Autowired
    private MongoOperations mongoOperations;
    private Customer deletedCustomer;
    public @Override void onBeforeSave(BeforeSaveEvent<Customer> event) {
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(),
                new CascadeSaveCallback(source, mongoOperations));
    }
    public @Override void onBeforeDelete(BeforeDeleteEvent<Customer> event) {
        final Object id = Objects.requireNonNull(event.getDocument()).get("_id");
        deletedCustomer = mongoOperations.findById(id, Customer.class);
    }
    public @Override void onAfterDelete(AfterDeleteEvent<Customer> event) {
        ReflectionUtils.doWithFields(Customer.class,
                new CascadeDeleteCallback(deletedCustomer, mongoOperations));
    }
}