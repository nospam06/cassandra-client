package org.cassandra.client.web;

import lombok.RequiredArgsConstructor;
import org.cassandra.client.api.CassandraClient;
import org.cassandra.client.data.KeyspaceRequest;
import org.cassandra.client.data.SessionRequest;
import org.cassandra.client.data.TableData;
import org.cassandra.client.data.TableMetaData;
import org.cassandra.client.data.TableRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/cassandra", produces = MediaType.APPLICATION_JSON_VALUE)
public class CassandraClientController {
    private final CassandraClient client;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<String>> login(@RequestBody SessionRequest request) {
         return client.connect(request.getUrl(), request.getPort(), request.getUserId(), request.getPassword());
    }

    @PostMapping(value = "/keyspace", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<String> createKeyspace(@RequestBody KeyspaceRequest request) {
        return client.createKeyspace(request.getSessionUuid(), request.getKeyspace());
    }

    @GetMapping(value = "/keyspace", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listKeyspace(@RequestBody KeyspaceRequest request) {
        return client.listTables(request.getSessionUuid(), request.getKeyspace());
    }

    @GetMapping(value = "/table", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TableData table(@RequestBody TableRequest request) {
        return client.tableData(request.getSessionUuid(), request.getKeyspace(), request.getTable());
    }

    @GetMapping(value = "/table/meta", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<TableMetaData> tableMetadata(@RequestBody TableRequest request) {
        return client.tableMetaData(request.getSessionUuid(), request.getKeyspace(), request.getTable());
    }
}
