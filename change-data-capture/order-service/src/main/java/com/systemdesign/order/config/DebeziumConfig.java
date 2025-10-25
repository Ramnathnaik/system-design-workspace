package com.systemdesign.order.config;

import io.debezium.config.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class DebeziumConfig {

    @Value("${debezium.connector.database.hostname}")
    private String dbHostname;

    @Value("${debezium.connector.database.port}")
    private String dbPort;

    @Value("${debezium.connector.database.user}")
    private String dbUser;

    @Value("${debezium.connector.database.password}")
    private String dbPassword;

    @Value("${debezium.connector.database.dbname}")
    private String dbName;

    @Value("${debezium.connector.database.server.id}")
    private String serverId;

    @Value("${debezium.connector.database.server.name}")
    private String serverName;

    @Value("${debezium.connector.offset.storage}")
    private String offsetStorage;

    @Value("${debezium.connector.offset.file}")
    private String offsetFile;

    @Value("${debezium.connector.offset.flush.interval.ms}")
    private String offsetFlushInterval;

    @Bean
    public Configuration debeziumConfiguration() {
        return Configuration.create()
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("offset.storage", offsetStorage)
                .with("offset.storage.file.filename", offsetFile)
                .with("offset.flush.interval.ms", offsetFlushInterval)
                .with("name", "order-mysql-connector")
                .with("database.server.name", serverName)
                .with("database.server.id", serverId)
                .with("database.hostname", dbHostname)
                .with("database.port", dbPort)
                .with("database.user", dbUser)
                .with("database.password", dbPassword)
                .with("database.include.list", dbName)
                .with("table.include.list", dbName + ".orders")
                .with("include.schema.changes", "false")
                .with("database.allowPublicKeyRetrieval", "true")
                .with("database.history", "io.debezium.relational.history.MemoryDatabaseHistory")
                .with("topic.prefix", "order-cdc")
                .build();
    }
}
