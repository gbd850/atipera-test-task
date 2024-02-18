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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RepoService {

    private static final Logger log = LoggerFactory.getLogger(RepoService.class);

    @Value("${api.url}")
    private String apiUrl;

//    private WebClient webClient;
//
//    public RepoService() {
//        this.webClient = WebClient.builder().baseUrl(apiUrl).build();
//    }

    @Async
    public CompletableFuture<List<Repo>> getUserRepositories(String username, String acceptHeader) {

        WebClient webClient = WebClient.builder().baseUrl(apiUrl).build();

        log.info("getUserRepositories : {}", Thread.currentThread());

        RepoResponse[] res = webClient.get()
                .uri(String.format("/users/%s/repos", username))
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND, "User with given username does not exist", null, null, null, null))
                )
                .bodyToMono(RepoResponse[].class)
                .block();

        List<RepoResponse> reposResponse = Arrays.stream(res).filter(el -> !el.getFork()).toList();

        List<Repo> repos = reposResponse.stream()
                .map(el -> Repo.builder()
                        .repositoryName(el.getName())
                        .ownerLogin(el.getOwner().getLogin())
                        .build())
                .toList();

        for (Repo repo : repos) {

            List<Branch> branches = getRepoBranches(username, repo.getRepositoryName(), acceptHeader).join();

            repo.setBranches(branches);
        }

        return CompletableFuture.completedFuture(repos);
    }

    @Async
    private CompletableFuture<List<Branch>> getRepoBranches(String username, String repoName, String acceptHeader) {

        WebClient webClient = WebClient.builder().baseUrl(apiUrl).build();

        log.info("getRepoBranches : {}", Thread.currentThread());

        BranchResponse[] branchResponseses = webClient.get()
                .uri(String.format("/repos/%s/%s/branches", username, repoName))
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND, "User with given username does not exist", null, null, null, null))
                )
                .bodyToMono(BranchResponse[].class)
                .block();

        return CompletableFuture.completedFuture(Arrays.stream(branchResponseses)
                .map(branch -> new Branch(branch.getName(), branch.getCommit().getSha()))
                .toList());
    }
}
