package com.test.atipera.dto;

import lombok.Data;

@Data
public class BranchResponse {
    private String name;
    private Commit commit;
}
