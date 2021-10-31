package org.cassandra.client.service;

import lombok.extern.slf4j.Slf4j;
import org.cassandra.client.data.TableData;
import org.cassandra.client.data.TableMetaData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Disabled
class CassandraClientTest {
    private final CassandraClientImpl cassandraClient = new CassandraClientImpl();

    @Test
    void connect() {
        Map<String, List<String>> keySpaces = cassandraClient.connect("localhost", 9042, "foo", "bar");
        String sessionUuid = keySpaces.keySet().iterator().next();
        keySpaces.values().stream().flatMap(Collection::stream).forEach(ks -> {
            List<String> tables = cassandraClient.listTables(sessionUuid, ks);
            log.info("Tables in keyspace {} -> {}", ks, tables);
        });
    }

    @Test
    void tableMetaData() {
        Map<String, List<String>> keySpaces = cassandraClient.connect("localhost", 9042, "foo", "bar");
        String sessionUuid = keySpaces.keySet().iterator().next();
        List<TableMetaData> metaData = cassandraClient.tableMetaData(sessionUuid, "system_schema", "columns");
        log.info("{}", metaData);
    }

    @Test
    void tableData() {
        Map<String, List<String>> keySpaces = cassandraClient.connect("localhost", 9042, "foo", "bar");
        String sessionUuid = keySpaces.keySet().iterator().next();
        TableData tableData = cassandraClient.tableData(sessionUuid, "mydb", "address_book");
        log.info("{}", tableData);
    }
}