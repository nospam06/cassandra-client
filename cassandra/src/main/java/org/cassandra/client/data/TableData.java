package org.cassandra.client.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class TableData {
    private List<TableMetaData> columns;
    private List<Row> rows;
}
