package com.test.atipera.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8081)
public class RepoControllerTest {
    @Autowired
    private WebTestClient client;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("api.url", () -> "http://localhost:8081");
    }

    @Test
    public void givenValidUsername_whenRequest_thenStatus200() throws IOException {

        // given
        String username = "gbd850";

        String repoResponseBody = IOUtils.resourceToString("/files/repo-service-test/correct-repo-response.json", StandardCharsets.UTF_8);
        String branchResponseBody = IOUtils.resourceToString("/files/repo-service-test/correct-branch-response.json", StandardCharsets.UTF_8);

        stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(
                        aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(repoResponseBody)
                )
        );

        stubFor(WireMock.get(urlMatching("\\/repos\\/.+\\/.+\\/branches"))
                .willReturn(
                        aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(branchResponseBody)
                )
        );

        // when
        client.get()
                .uri(String.format("/api/repos/%s", username))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        // then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].repositoryName").isNotEmpty()
                .jsonPath("$[0].ownerLogin").isNotEmpty()
                .jsonPath("$[0].branches").isArray()
                .jsonPath("$[0].branches[0].name").isNotEmpty()
                .jsonPath("$[0].branches[0].lastCommitSha").isNotEmpty();
    }

    @Test
    public void givenInvalidUsername_whenRequest_thenStatus404() {

        // given
        String username = "gbd850";

        stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(
                        aResponse()
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                )
        );

        // when
        client.get()
                .uri(String.format("/api/repos/%s", username))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        // then
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("User with given username does not exist");
    }
}
