package com.santik.bookchecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santik.bookchecker.producer.login.model.AuthToken;
import com.santik.bookchecker.producer.login.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class LogInIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${http.username}")
    String username;

    @Value("${http.password}")
    String password;

    private final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void testUnauthorised() throws Exception {
        String incorrectUser = "user";
        String incorrectPassword = "password";

        User user = new User();
        user.username(incorrectUser);
        user.password(incorrectPassword);

        mockMvc.perform(
                        post("/auth/login")
                                .content(mapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorised() throws Exception {

        User user = new User();
        user.username(username);
        user.password(password);

        MvcResult mvcResult = mockMvc.perform(
                        post("/auth/login")
                                .content(mapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        log.info(content);
        AuthToken authToken = new ObjectMapper().readValue(content, AuthToken.class);
        assertNotNull(authToken);

    }


}
