package com.test.atipera;

import com.test.atipera.model.Repo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoServiceTest {

    private RepoService repoService;

    @BeforeEach
    void setUp() {
        repoService = new RepoService();
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

}
