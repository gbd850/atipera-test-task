package com.test.atipera.service;

import com.test.atipera.dto.BranchResponse;
import com.test.atipera.dto.RepoResponse;
import com.test.atipera.model.Branch;
import com.test.atipera.model.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RepoService {

    private static final Logger log = LoggerFactory.getLogger(RepoService.class);
    private final RestClient webClient;
    private String apiUrl;

    public RepoService(@Value("${api.url}") String apiUrl) {
        this.webClient = RestClient.builder().baseUrl(apiUrl).build();
    }

    @Async
    public CompletableFuture<List<Repo>> getUserRepositories(String username, String acceptHeader) {

        log.info("getUserRepositories : {}", Thread.currentThread());

        RepoResponse[] res = webClient.get()
                .uri(String.format("/users/%s/repos", username))
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            throw new RestClientResponseException(
                                    "User with given username does not exist",
                                    HttpStatus.NOT_FOUND.value(),
                                    HttpStatus.NOT_FOUND.name(),
                                    null,
                                    null,
                                    null
                            );
                        }
                )
                .body(RepoResponse[].class);

        List<RepoResponse> reposResponse = Arrays.stream(res).filter(el -> !el.fork()).toList();

        List<Repo> repos = reposResponse.stream()
                .map(el -> new Repo(el.name(), el.owner().login(), new ArrayList<>()))
                .toList();

        for (Repo repo : repos) {

            List<Branch> branches = getRepoBranches(username, repo.repositoryName(), acceptHeader).join();

            repo.branches().clear();
            repo.branches().addAll(branches);
        }

        return CompletableFuture.completedFuture(repos);
    }

    @Async
    private CompletableFuture<List<Branch>> getRepoBranches(String username, String repoName, String acceptHeader) {

        log.info("getRepoBranches : {}", Thread.currentThread());

        BranchResponse[] branchResponses = webClient.get()
                .uri(String.format("/repos/%s/%s/branches", username, repoName))
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            throw new RestClientResponseException(
                                    "User with given username does not exist",
                                    HttpStatus.NOT_FOUND.value(),
                                    HttpStatus.NOT_FOUND.name(),
                                    null,
                                    null,
                                    null
                            );
                        }
                )
                .body(BranchResponse[].class);

        return CompletableFuture.completedFuture(Arrays.stream(branchResponses)
                .map(branch -> new Branch(branch.name(), branch.commit().sha()))
                .toList());
    }
}
