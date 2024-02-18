package com.test.atipera.controller;

import com.test.atipera.model.Branch;
import com.test.atipera.model.Repo;
import com.test.atipera.service.RepoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/repos")
@AllArgsConstructor
public class RepoController {

    private RepoService repoService;

    @GetMapping("{username}")
    public ResponseEntity<CompletableFuture<List<Repo>>> getUserRepositories(@PathVariable String username, @RequestHeader("Accept") String acceptHeader) {
        return new ResponseEntity<>(repoService.getUserRepositories(username, acceptHeader), HttpStatus.OK);
    }

}
