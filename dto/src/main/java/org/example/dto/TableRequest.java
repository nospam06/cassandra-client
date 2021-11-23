package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableRequest {
    private String sessionUuid;
    private String keyspace;
    private String table;
}
