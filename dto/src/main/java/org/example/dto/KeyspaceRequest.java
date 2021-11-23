package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyspaceRequest {
    private String sessionUuid;
    private String keyspace;
}
