package org.cassandra.client.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class TableRequest {
    private String sessionUuid;
    private String keyspace;
    private String table;
}
