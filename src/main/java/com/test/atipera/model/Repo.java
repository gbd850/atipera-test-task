package com.test.atipera.model;

import java.util.List;

public record Repo(
        String repositoryName,
        String ownerLogin,
        List<Branch> branches
) {
}
