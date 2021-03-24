package com.kbui.ceres.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@Slf4j
public class MongoConnectionConfiguration {
  final String connectionString = System.getenv("MONGO_URL");
  final String databaseName = "ceres";

  @Bean
  public MongoClient mongoClient() {
    return MongoClients.create(connectionString);
  }

  @Bean
  public MongoDatabaseFactory mongoDatabaseFactory() {
    return new SimpleMongoClientDatabaseFactory(mongoClient(), databaseName);
  }

  @Bean
  public MongoTemplate mongoTemplate() {
    return new MongoTemplate(mongoClient(), databaseName);
  }
}
