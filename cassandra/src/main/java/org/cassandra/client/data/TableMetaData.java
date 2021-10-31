package org.cassandra.client.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableMetaData {
    private String column;
    private String type;
}
