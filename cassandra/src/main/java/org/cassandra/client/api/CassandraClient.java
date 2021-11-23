package org.cassandra.client.api;

import org.example.dto.KeyspaceResponse;
import org.example.dto.SessionResponse;
import org.example.dto.TableResponse;

public interface CassandraClient {
    SessionResponse connect(String url, int port, String userId, String password);

    KeyspaceResponse createKeyspace(String sessionUuid, String keyspace);

    KeyspaceResponse listTables(String sessionUuid, String keyspace);

    TableResponse tableMetaData(String sessionUuid, String keyspace, String tableName);

    TableResponse tableData(String sessionUuid, String keyspace, String tableName);

    void executeQuery(String sessionUuid, String query);
}
