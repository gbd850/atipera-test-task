package com.test.atipera.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RepoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void givenValidUsername_whenRequest_thenStatus200() throws Exception {
        // given
        String username = "gbd850";
        // when
        ResultActions request = mvc.perform(
                get(String.format("/api/repos/%s", username))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-GitHub-Api-Version", "2022-11-28")
        );
        // then
        request
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].repositoryName").value(anyString()))
                .andExpect(jsonPath("$[*].ownerLogin").value(anyString()))
                .andExpect(jsonPath("$[*].branches").isArray())
                .andExpect(jsonPath("$[*].branches[*].name").value(anyString()))
                .andExpect(jsonPath("$[*].branches[*].lastCommitSha").value(anyString()));
    }
}
