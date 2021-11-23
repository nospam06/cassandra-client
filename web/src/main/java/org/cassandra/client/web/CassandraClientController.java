package org.cassandra.client.web;

import org.example.dto.KeyspaceRequest;
import org.example.dto.KeyspaceResponse;
import org.example.dto.SessionRequest;
import lombok.RequiredArgsConstructor;
import org.cassandra.client.api.CassandraClient;
import org.example.dto.SessionResponse;
import org.example.dto.TableRequest;
import org.example.dto.TableResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping(path = "/api/cassandra", produces = MediaType.APPLICATION_JSON_VALUE)
public class CassandraClientController {
    private final CassandraClient client;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public SessionResponse login(@RequestBody SessionRequest request) {
         return client.connect(request.getUrl(), request.getPort(), request.getUserId(), request.getPassword());
    }

    @PostMapping(value = "/keyspace", consumes = MediaType.APPLICATION_JSON_VALUE)
    public KeyspaceResponse listTables(@RequestBody KeyspaceRequest request) {
        return client.listTables(request.getSessionUuid(), request.getKeyspace());
    }

    @PostMapping(value = "/table", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TableResponse tableQuery(@RequestBody TableRequest request) {
        return client.tableData(request.getSessionUuid(), request.getKeyspace(), request.getTable());
    }

    @PostMapping(value = "/table/meta", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TableResponse tableMetadata(@RequestBody TableRequest request) {
        return client.tableMetaData(request.getSessionUuid(), request.getKeyspace(), request.getTable());
    }
}
