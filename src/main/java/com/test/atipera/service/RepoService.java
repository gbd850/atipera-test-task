package com.test.atipera.service;

import com.test.atipera.model.Branch;
import com.test.atipera.model.Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepoService {
    public List<Repo> getUserRepositories(String username, String acceptHeader) {
        //TODO fetch data from GitHub API
        return List.of(new Repo("", "", List.of(new Branch("", ""))));
    }
}
