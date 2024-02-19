package com.test.atipera.dto;

public record RepoResponse(
        String name,
        Owner owner,
        Boolean fork
) {
}
