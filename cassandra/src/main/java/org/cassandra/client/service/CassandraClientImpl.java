package org.cassandra.client.service;

import com.datastax.oss.driver.api.core.AsyncAutoCloseable;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.cassandra.client.api.CassandraClient;
import org.example.dto.KeyspaceResponse;
import org.example.dto.SessionResponse;
import org.example.dto.TableMetaData;
import org.example.dto.TableResponse;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CassandraClientImpl implements CassandraClient {
    private static final String SYSTEM_SCHEMA = "system_schema";
    private static final String DATACENTER = "datacenter1";
    private static final String LIST_KEYSPACES = "SELECT keyspace_name FROM keyspaces";
    private static final String CREATE_KEYSPACE = "create keyspace if not exists %s";
    private static final String LIST_TABLES = "select table_name from system_schema.tables where keyspace_name = '%s'";
    private static final String SELECT_QUERY = "select * from %s.%s";
    private final ConcurrentHashMap<String, CqlSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public SessionResponse connect(String url, int port, String userId, String password) {
        CqlSession cqlSession = createSession(url, port);
        String sessionUuid = UUID.randomUUID().toString();
        sessionMap.put(sessionUuid, cqlSession);
        ResultSet resultSet = executeInternal(sessionUuid, LIST_KEYSPACES);
        List<String> keyspaces = new ArrayList<>();
        resultSet.forEach(rs -> keyspaces.add(rs.getString(0)));
        return new SessionResponse(sessionUuid, keyspaces);
    }

    @Override
    public KeyspaceResponse createKeyspace(String sessionUuid, String keyspace) {
        executeInternal(sessionUuid, String.format(CREATE_KEYSPACE, keyspace));
        return listTable(sessionUuid, keyspace);
    }

    @Override
    public KeyspaceResponse listTable(String sessionUuid, String keyspace) {
        ResultSet resultSet = executeInternal(sessionUuid, String.format(LIST_TABLES, keyspace));
        List<String> names = new ArrayList<>();
        resultSet.forEach(rs -> names.add(rs.getString(0)));
        return new KeyspaceResponse(keyspace, names);
    }

    @Override
    public TableResponse tableData(String sessionUuid, String keyspace, String tableName) {
        ResultSet resultSet = executeInternal(sessionUuid, String.format(SELECT_QUERY, keyspace, tableName));
        List<TableMetaData> metaData = createMetaData(resultSet);
        List<List<String>> rows = new ArrayList<>();
        resultSet.forEach(r -> {
            List<String> row = new ArrayList<>();
            rows.add(row);
            metaData.forEach(m -> row.add(Optional.ofNullable(r.getObject(m.getName())).map(Object::toString).orElse("")));
        });
        return new TableResponse(tableName, metaData, rows);
    }

    private List<TableMetaData> createMetaData(ResultSet resultSet) {
        List<TableMetaData> metaData = new ArrayList<>();
        resultSet.getColumnDefinitions().forEach(c -> metaData.add(new TableMetaData(c.getName().toString(), c.getType().toString())));
        return metaData;
    }

    @Override
    public void executeQuery(String sessionUuid, String query) {
        executeInternal(sessionUuid, query);
    }

    private ResultSet executeInternal(String sessionUuid, String query) {
        CqlSession cqlSession = sessionMap.get(sessionUuid);
        return cqlSession.execute(query);
    }

    private CqlSession createSession(String url, int port) {
        return CqlSession.builder().addContactPoint(new InetSocketAddress(url, port))
                .withKeyspace(SYSTEM_SCHEMA).withLocalDatacenter(DATACENTER).build();
    }

    @PreDestroy
    public void shutdown() {
        sessionMap.values().forEach(AsyncAutoCloseable::close);
        sessionMap.clear();
    }
}
