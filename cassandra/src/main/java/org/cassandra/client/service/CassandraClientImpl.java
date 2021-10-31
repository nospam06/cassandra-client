package org.cassandra.client.service;

import com.datastax.oss.driver.api.core.AsyncAutoCloseable;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.cassandra.client.api.CassandraClient;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CassandraClientImpl implements CassandraClient {
    private static final String SYSTEM_SCHEMA = "system_schema";
    private static final String DATACENTER = "datacenter1";
    private static final String LIST_KEYSPACES = "SELECT keyspace_name FROM keyspaces";
    private static final String CREATE_KEYSPACE = "create keyspace if not exists %s";
    private static final String LIST_TABLES = "select table_name from system_schema.tables where keyspace_name = '%s'";
    private static final String TABLE_METADATA = "select * from system_schema.columns where keyspace_name = '%s' and table_name = '%s'";
    private static final String SELECT_QUERY = "select * from %s.%s";
    private final ConcurrentHashMap<String, CqlSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public Map<String, List<String>> connect(String url, int port, String userId, String password) {
        CqlSession cqlSession = createSession(url, port);
        String sessionUuid = UUID.randomUUID().toString();
        sessionMap.put(sessionUuid, cqlSession);
        ResultSet resultSet = executeInternal(sessionUuid, LIST_KEYSPACES);
        List<String> keyspaces = new ArrayList<>();
        resultSet.forEach(rs -> keyspaces.add(rs.getString(0)));
        return Map.of(sessionUuid, keyspaces);
    }

    @Override
    public List<String> createKeyspace(String sessionUuid, String keyspace) {
        executeInternal(sessionUuid, String.format(CREATE_KEYSPACE, keyspace));
        return listTables(sessionUuid, keyspace);
    }

    @Override
    public List<String> listTables(String sessionUuid, String keyspace) {
        ResultSet resultSet = executeInternal(sessionUuid, String.format(LIST_TABLES, keyspace));
        List<String> names = new ArrayList<>();
        resultSet.forEach(rs -> names.add(rs.getString(0)));
        return names;
    }

    @Override
    public LinkedHashMap<String, String> tableMetaData(String sessionUuid, String keyspace, String tableName) {
        ResultSet resultSet = executeInternal(sessionUuid, String.format(TABLE_METADATA, keyspace, tableName));
        return createMetaData(resultSet);
    }

    @Override
    public List<LinkedHashMap<String, String>> tableData(String sessionUuid, String keyspace, String tableName) {
        ResultSet resultSet = executeInternal(sessionUuid, String.format(SELECT_QUERY, keyspace, tableName));
        LinkedHashMap<String, String> metaData = createMetaData(resultSet);
        List<LinkedHashMap<String, String>> rows = new ArrayList<>();
        resultSet.forEach(r -> {
            LinkedHashMap<String, String> row = new LinkedHashMap<>();
            rows.add(row);
            metaData.forEach((k, v) -> {
                String column = metaData.get(k);
                Optional.ofNullable(r.getObject(column)).map(Object::toString)
                        .ifPresent(data -> row.put(column, data));
            });
        });
        return rows;
    }

    private LinkedHashMap<String, String> createMetaData(ResultSet resultSet) {
        LinkedHashMap<String, String> metaData = new LinkedHashMap<>();
        resultSet.forEach(c -> metaData.put(c.getString("column_name"), c.getString("type")));
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
