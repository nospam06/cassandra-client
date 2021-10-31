package org.cassandra.client.service;

import com.datastax.oss.driver.api.core.AsyncAutoCloseable;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.cassandra.client.api.CassandraClient;
import org.cassandra.client.data.Row;
import org.cassandra.client.data.RowData;
import org.cassandra.client.data.TableData;
import org.cassandra.client.data.TableMetaData;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CassandraClientImpl implements CassandraClient {
    private static final String LIST_KEYSPACES = "SELECT keyspace_name FROM keyspaces";
    private static final String CREATE_KEYSPACE = "create keyspace if not exists %s";
    private static final String SYSTEM_SCHEMA = "system_schema";
    private static final String LIST_METADATA = "select * from system_schema.columns where keyspace_name = '%s' and table_name = '%s'";
    private static final String LIST_TABLES = "select table_name from system_schema.tables where keyspace_name = '%s'";
    private static final String SELECT_QUERY = "select * from %s.%s";
    private static final String DATACENTER = "datacenter1";
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
    public List<TableMetaData> tableMetaData(String sessionUuid, String keyspace, String tableName) {
        ResultSet resultSet = executeInternal(sessionUuid, String.format(LIST_METADATA, keyspace, tableName));
        return createMetaData(resultSet);
    }

    @Override
    public TableData tableData(String sessionUuid, String keyspace, String tableName) {
        ResultSet resultSet = executeInternal(sessionUuid, String.format(SELECT_QUERY, keyspace, tableName));
        List<TableMetaData> metaData = createMetaData(resultSet);
        TableData tableData = new TableData();
        List<Row> rows = new ArrayList<>();
        tableData.setColumns(metaData);
        tableData.setRows(rows);
        resultSet.forEach(r -> {
            Row row = new Row();
            rows.add(row);
            for (int i = 0; i < metaData.size(); ++i) {
                TableMetaData tableMetaData = metaData.get(i);
                Optional.ofNullable(r.getObject(i)).map(Object::toString)
                        .ifPresent(data -> {
                            RowData rowData = new RowData(tableMetaData.getColumn(), data);
                            row.getRows().add(rowData);
                        });
            }
        });
        return tableData;
    }

    private List<TableMetaData> createMetaData(ResultSet resultSet) {
        List<TableMetaData> metaData = new ArrayList<>();
        resultSet.getColumnDefinitions().forEach(c -> {
            TableMetaData tableMetaData = new TableMetaData(c.getName().asCql(true), c.getType().asCql(false, true));
            metaData.add(tableMetaData);
        });
        return metaData;
    }

    @Override
    public void executeQuery(String sessionUuid, String query) {
        executeInternal(sessionUuid, query);
    }

    private ResultSet executeInternal(String sessionUuid, String query) {
        log.info("{}", query);
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
