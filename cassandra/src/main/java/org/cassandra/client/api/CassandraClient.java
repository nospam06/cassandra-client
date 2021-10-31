package org.cassandra.client.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface CassandraClient {
    Map<String, List<String>> connect(String url, int port, String userId, String password);

    List<String> createKeyspace(String sessionUuid, String keyspace);

    List<String> listTables(String sessionUuid, String keyspace);

    LinkedHashMap<String, String> tableMetaData(String sessionUuid, String keyspace, String tableName);

    List<LinkedHashMap<String, String>> tableData(String sessionUuid, String keyspace, String tableName);

    void executeQuery(String sessionUuid, String query);
}
