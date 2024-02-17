package com.test.atipera.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repos")
public class RepoController {

    @GetMapping("{username}")
    public ResponseEntity getUserRepositories(@PathVariable String username) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
