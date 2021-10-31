package org.cassandra.client.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RowData {
    private String column;
    private String value;
}
