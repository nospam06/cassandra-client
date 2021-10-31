package org.cassandra.client.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class SessionRequest {
    private String url;
    private int port;
    private String userId;
    private String password;
}
