package com.test.atipera.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Repo {
    private String repositoryName;
    private String ownerLogin;
    private List<Branch> branches;
}
