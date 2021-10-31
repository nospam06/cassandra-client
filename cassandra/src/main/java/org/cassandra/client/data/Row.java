package org.cassandra.client.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Row {
    private List<RowData> rows = new ArrayList<>();
}
