package com.test.atipera.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Branch {
    private String name;
    private String lastCommitSha;
}
