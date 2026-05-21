package com.automation.api.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserRequest {
    private String name;
    private String job;
}
