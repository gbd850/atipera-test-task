package com.test.atipera.integration;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.test.atipera.model.Repo;
import com.test.atipera.service.RepoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8081)
public class RepoServiceTest {

    @Autowired
    private RepoService repoService;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("api.url", () -> "http://localhost:8081");
    }

    @Test
    public void givenValidUsername_whenGetUserRepositories_thenReturnData() throws IOException {
        // given
        String username = "gbd850";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        String repoResponseBody = IOUtils.resourceToString("/files/repo-service-test/correct-repo-response.json", StandardCharsets.UTF_8);
        String branchResponseBody = IOUtils.resourceToString("/files/repo-service-test/correct-branch-response.json", StandardCharsets.UTF_8);

        stubFor(get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(
                        aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(repoResponseBody)
                )
        );

        stubFor(get(urlMatching("\\/repos\\/.+\\/.+\\/branches"))
                .willReturn(
                        aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(branchResponseBody)
                )
        );
        // when
        List<Repo> expected = repoService.getUserRepositories(username, acceptHeader).join();
        // then
        assertThat(expected).isNotNull();
        assertThat(expected).isNotEmpty();
        assertThat(expected).hasOnlyElementsOfType(Repo.class);
        assertThat(expected).allMatch(el -> el.branches() != null);
        assertThat(expected).allMatch(el -> !el.branches().isEmpty());
    }

    @Test
    public void givenInvalidUsername_whenGetUserRepositories_thenThrowException() {
        // given
        String username = "gbd850";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;

        stubFor(get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(
                        aResponse()
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
        );

        // when
        // then
        assertThatThrownBy(() -> repoService.getUserRepositories(username, acceptHeader).get())
                .isInstanceOf(ExecutionException.class)
                .cause()
                    .isInstanceOf(WebClientResponseException.class)
                    .hasMessageContaining("User with given username does not exist")
                    .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }

}
