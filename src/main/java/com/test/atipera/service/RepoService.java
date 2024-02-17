package com.test.atipera.service;

import com.test.atipera.dto.BranchResponse;
import com.test.atipera.dto.RepoResponse;
import com.test.atipera.model.Branch;
import com.test.atipera.model.Repo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@AllArgsConstructor
public class RepoService {

    private WebClient.Builder webClientBuilder;

    public List<Repo> getUserRepositories(String username, String acceptHeader) {
        RepoResponse[] res = webClientBuilder.build().get()
                .uri(String.format("https://api.github.com/users/%s/repos", username))
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
            BranchResponse[] branchResponseses = webClientBuilder.build().get()
                    .uri(String.format("https://api.github.com/repos/%s/%s/branches", username, repo.getRepositoryName()))
                    .header(HttpHeaders.ACCEPT, acceptHeader)
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND, "User with given username does not exist", null, null, null, null))
                    )
                    .bodyToMono(BranchResponse[].class)
                    .block();

            List<Branch> branches = Arrays.stream(branchResponseses)
                    .map(branch -> new Branch(branch.getName(), branch.getCommit().getSha()))
                    .toList();

            repo.setBranches(branches);
        }

        return repos;
    }
}
