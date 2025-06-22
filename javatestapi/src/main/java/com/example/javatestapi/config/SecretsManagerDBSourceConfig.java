package com.example.javatestapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Profile("!test")  // Exclude this config from test profile to avoid connection attempts during unit tests
public class SecretsManagerDBSourceConfig {

    // Injected from application.properties
    @Value("${aws.secret.name}")
    private String secretName;

    @Value("${aws.rds.db.identifier}")
    private String dbIdentifier;

    @Value("${aws.region:us-west-2}")  // Default if not provided
    private String region;

    @Bean
    public DataSource dataSource() {
        try {
            Region awsRegion = Region.of(region);

            // ===== Fetch DB credentials from AWS Secrets Manager =====
            SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                    .region(awsRegion)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            GetSecretValueResponse secretResponse = secretsClient.getSecretValue(GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build());
            
            // Deserialize secret JSON into a Map
            Map<String, String> secrets = new ObjectMapper()
                    .readValue(secretResponse.secretString(), Map.class);

            String username = secrets.get("username");
            String password = secrets.get("password");

            secretsClient.close();

            // ===== Fetch RDS endpoint info (hostname, port, db name) =====
            RdsClient rdsClient = RdsClient.builder()
                    .region(awsRegion)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            DescribeDbInstancesResponse dbResponse = rdsClient.describeDBInstances(DescribeDbInstancesRequest.builder()
                    .dbInstanceIdentifier(dbIdentifier)
                    .build());

            DBInstance dbInstance = dbResponse.dbInstances().get(0);
            String host = dbInstance.endpoint().address();
            int port = dbInstance.endpoint().port();
            String dbName = dbInstance.dbName() != null ? dbInstance.dbName() : "postgres"; // fallback

            rdsClient.close();

            // ===== Build and configure HikariCP DataSource =====
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);

            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbcUrl);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setDriverClassName("org.postgresql.Driver");

            System.out.println("DataSource configured via AWS Secrets Manager and RDS lookup.");
            return ds;

        } catch (Exception e) {
            System.err.println("Failed to configure DataSource: " + e.getMessage());
            throw new RuntimeException("Database setup failed", e);
        }
    }
}
