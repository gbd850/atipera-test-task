package com.test.atipera.service;

import com.test.atipera.model.Repo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class RepoService {

    private WebClient.Builder webClientBuilder;

    public List<Repo> getUserRepositories(String username, String acceptHeader) {
        //TODO fetch data from GitHub API
        RepoResponse[] repos = webClientBuilder.build().get()
                .uri(String.format("https://api.github.com/users/%s/repos", username))
                .header(HttpHeaders.ACCEPT, acceptHeader)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new WebClientResponseException(HttpStatus.NOT_FOUND, "User with given username does not exist", null, null, null, null))
                )
                .bodyToMono(RepoResponse[].class)
                .block();
        return null;
    }
}
