package com.test.atipera.controller;

import com.test.atipera.model.Branch;
import com.test.atipera.model.Repo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/repos")
public class RepoController {

    @GetMapping("{username}")
    public ResponseEntity<List<Repo>> getUserRepositories(@PathVariable String username) {
        return new ResponseEntity<>(List.of(new Repo("", "", List.of(new Branch("", "")))), HttpStatus.OK);
    }

}
