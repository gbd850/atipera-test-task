package com.test.atipera.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Repo {
    private String repositoryName;
    private String ownerLogin;
    private List<Branch> branches;
}
