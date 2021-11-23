package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionRequest {
    private String url;
    private int port;
    private String userId;
    private String password;
}
