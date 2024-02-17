package com.test.atipera.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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
        );
        // then
        request
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].repositoryName").isString())
                .andExpect(jsonPath("$[0].ownerLogin").isString())
                .andExpect(jsonPath("$[0].branches").isArray())
                .andExpect(jsonPath("$[0].branches[0].name").isString())
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha").isString());
    }

    @Test
    public void givenInvalidUsername_whenRequest_thenStatus404() throws Exception {
        // given
        String username = " ";
        // when
        ResultActions request = mvc.perform(
                get(String.format("/api/repos/%s", username))
                        .accept(MediaType.APPLICATION_JSON)
        );
        // then
        request
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User with given username does not exist"));
    }
}
