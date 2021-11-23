package org.cassandra.client.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.KeyspaceResponse;
import org.example.dto.SessionResponse;
import org.example.dto.TableResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
@Disabled
class CassandraClientTest {
    private final CassandraClientImpl cassandraClient = new CassandraClientImpl();

    @Test
    void connect() {
        SessionResponse response = cassandraClient.connect("localhost", 9042, "foo", "bar");
        String sessionUuid = response.getSessionUuid();
        response.getKeyspaces().forEach(ks -> {
            KeyspaceResponse tables = cassandraClient.listTables(sessionUuid, ks);
            log.info("Tables in keyspace {} -> {}", ks, tables);
        });
    }

    @Test
    void tableMetaData() {
        SessionResponse response = cassandraClient.connect("localhost", 9042, "foo", "bar");
        String sessionUuid = response.getSessionUuid();
        TableResponse metaData = cassandraClient.tableMetaData(sessionUuid, "system_schema", "columns");
        log.info("{}", metaData);
    }

    @Test
    void tableData() {
        SessionResponse response = cassandraClient.connect("localhost", 9042, "foo", "bar");
        String sessionUuid = response.getSessionUuid();
        TableResponse tableData = cassandraClient.tableData(sessionUuid, "mydb", "address_book");
        log.info("{}", tableData);
    }
}