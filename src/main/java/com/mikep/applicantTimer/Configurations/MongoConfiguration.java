package com.mikep.applicantTimer.Configurations;

import com.mikep.applicantTimer.Events.CustomerCascadeMongoEventListener;
import com.mikep.applicantTimer.Events.RoleCascadeMongoEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@EnableMongoRepositories(MongoConfiguration.REPO_PACKAGE)
@Configuration
public class MongoConfiguration {
    static final String REPO_PACKAGE = "com.mikep.applicantTimer.Repositories";
    public @Bean
    CustomerCascadeMongoEventListener customerCascadeMongoEventListener() {
        return new CustomerCascadeMongoEventListener();
    }

    public @Bean
    RoleCascadeMongoEventListener roleCascadeMongoEventListener() {
        return new RoleCascadeMongoEventListener();

    }
    @Autowired
    private MongoDatabaseFactory mongoFactory;

    @Autowired
    private MongoMappingContext mongoMappingContext;

    @Bean
    public MappingMongoConverter mongoConverter() throws Exception {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        //this is my customization
        mongoConverter.setMapKeyDotReplacement("_");
        return mongoConverter;
    }
}