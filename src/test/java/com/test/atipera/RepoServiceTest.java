package com.test.atipera;

import com.test.atipera.model.Repo;
import com.test.atipera.service.RepoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class RepoServiceTest {

    private RepoService repoService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @BeforeEach
    void setUp() {
        repoService = new RepoService(webClientBuilder);
    }

    @Test
    public void givenValidUsername_whenGetUserRepositories_thenReturnData() {
        // given
        String username = "gbd850";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;
        // when
        List<Repo> expected = repoService.getUserRepositories(username, acceptHeader);
        // then
        assertThat(expected).isNotNull();
        assertThat(expected).isNotEmpty();
        assertThat(expected).hasOnlyElementsOfType(Repo.class);
        assertThat(expected).allMatch(el -> el.getBranches() != null);
        assertThat(expected).allMatch(el -> !el.getBranches().isEmpty());
    }

    @Test
    public void givenInvalidUsername_whenGetUserRepositories_thenThrowException() {
        // given
        String username = "";
        String acceptHeader = MediaType.APPLICATION_JSON_VALUE;
        // when
        // then
        assertThatThrownBy(() -> repoService.getUserRepositories(username, acceptHeader))
                .isInstanceOf(WebClientResponseException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND)
                .hasMessageContaining("User with given username does not exist");
    }

}
