package com.systemdesign.inventory.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class DebeziumListener {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DebeziumListener(Configuration debeziumConfig, 
                           KafkaTemplate<String, String> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(debeziumConfig.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
        
        log.info("Key = {}, Value = {}", sourceRecord.key(), sourceRecord.value());

        Struct sourceRecordValue = (Struct) sourceRecord.value();
        
        if (sourceRecordValue != null) {
            String operation = sourceRecordValue.getString("op");
            
            if ("c".equals(operation) || "u".equals(operation)) {
                Struct after = (Struct) sourceRecordValue.get("after");
                Map<String, Object> payload = new HashMap<>();
                payload.put("operation", operation);
                payload.put("data", extractData(after));
                
                try {
                    String message = objectMapper.writeValueAsString(payload);
                    kafkaTemplate.send("inventory-updated", message);
                    log.info("Sent inventory CDC event to Kafka - Operation: {}", operation);
                } catch (Exception e) {
                    log.error("Error sending CDC event to Kafka", e);
                }
            }
        }
    }

    private Map<String, Object> extractData(Struct struct) {
        Map<String, Object> data = new HashMap<>();
        for (Field field : struct.schema().fields()) {
            data.put(field.name(), struct.get(field));
        }
        return data;
    }

    @PostConstruct
    private void start() {
        this.executor.execute(debeziumEngine);
        log.info("Debezium engine started for Inventory Service");
    }

    @PreDestroy
    private void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
            log.info("Debezium engine stopped for Inventory Service");
        }
    }
}
