package org.cassandra.client.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Row {
    private List<RowData> rows = new ArrayList<>();
}
