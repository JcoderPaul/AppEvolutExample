package me.oldboy.market.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import me.oldboy.market.config.test_data_source.TestContainerInit;
import me.oldboy.market.dto.jwt.JwtAuthRequest;
import me.oldboy.market.dto.jwt.JwtAuthResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerIT extends TestContainerInit {

    @Autowired
    private WebApplicationContext appContext;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private JwtAuthRequest jwtAuthRequest;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть JwtAuthResponse в JSON формате со сгенерированным JWT Token-ом")
    void loginUser_successLogin_andGetBackNotNullToken_Test() {
        jwtAuthRequest = JwtAuthRequest.builder()
                .email("admin@admin.ru")
                .password("1234")
                .build();

        String loginUserStr = objectMapper.writeValueAsString(jwtAuthRequest);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/market/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginUserStr))
                .andExpect(status().isOk())
                .andReturn();

        String strReturn = result.getResponse().getContentAsString();
        JwtAuthResponse jwtAuthResponse = objectMapper.readValue(strReturn, JwtAuthResponse.class);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(jwtAuthResponse).isNotNull();
        softly.assertThat(jwtAuthResponse.getId()).isEqualTo(1L);
        softly.assertThat(jwtAuthResponse.getEmail()).isEqualTo(jwtAuthRequest.getEmail());
        softly.assertThat(jwtAuthResponse.getAccessToken()).isNotNull();

        softly.assertAll();
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть 400 Bad Request при неверном email аутентификации")
    void loginUser_failedLogin_emailNotFound_Test() {
        jwtAuthRequest = JwtAuthRequest.builder()
                .email("malcolm@swordwing.de")
                .password("1234")
                .build();

        String loginUserStr = objectMapper.writeValueAsString(jwtAuthRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/market/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginUserStr))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User : " + jwtAuthRequest.getEmail() + " not found!")));
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть 400 Bad Request при неверном пароле аутентификации")
    void loginUser_failedLogin_wrongPassword_Test() {
        jwtAuthRequest = JwtAuthRequest.builder()
                .email("admin@admin.ru")
                .password("no_pass")
                .build();

        String loginUserStr = objectMapper.writeValueAsString(jwtAuthRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/market/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginUserStr))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Entered wrong password!")));
    }
}