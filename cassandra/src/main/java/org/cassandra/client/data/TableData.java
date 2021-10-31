package org.cassandra.client.data;

import lombok.Data;

import java.util.List;

@Data
public class TableData {
    private List<TableMetaData> columns;
    private List<Row> rows;
}
