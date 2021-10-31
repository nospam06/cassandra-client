package org.cassandra.client.api;

import org.cassandra.client.data.TableData;
import org.cassandra.client.data.TableMetaData;

import java.util.List;
import java.util.Map;

public interface CassandraClient {
    Map<String, List<String>> connect(String url, int port, String userId, String password);

    List<String> createKeyspace(String sessionUuid, String keyspace);

    List<String> listTables(String sessionUuid, String keyspace);

    List<TableMetaData> tableMetaData(String sessionUuid, String keyspace, String tableName);

    TableData tableData(String sessionUuid, String keyspace, String tableName);

    void executeQuery(String sessionUuid, String query);
}
