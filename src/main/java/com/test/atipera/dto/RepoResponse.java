package com.test.atipera.dto;

import lombok.Data;

@Data
public class RepoResponse {
    private String name;
    private Owner owner;
    private Boolean fork;
}
