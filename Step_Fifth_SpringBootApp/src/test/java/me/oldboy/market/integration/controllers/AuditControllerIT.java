package me.oldboy.market.integration.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import me.oldboy.market.auditor.core.dto.audit.AuditReadDto;
import me.oldboy.market.config.jwt_config.JwtTokenGenerator;
import me.oldboy.market.config.security_details.ClientDetailsService;
import me.oldboy.market.integration.TestContainerInit;
import me.oldboy.market.test_utils.TestConstant;
import me.oldboy.market.test_utils.TestTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditControllerIT extends TestContainerInit {
    @Autowired
    private WebApplicationContext appContext;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String validJwtToken, notValidToken;
    private Long existId, nonExistId;
    private String existUserEmail, nonExistUserEmail;
    private String BEARER_PREFIX = "Bearer ";
    private String HEADER_NAME = "Authorization";

    @BeforeEach
    @SneakyThrows
    void setUp() {
        existId = TestConstant.existId;
        nonExistId = TestConstant.nonExistId;
        existUserEmail = TestConstant.existUserEmail;
        nonExistUserEmail = TestConstant.nonExistUserEmail;

        mockMvc = MockMvcBuilders.webAppContextSetup(appContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        validJwtToken = TestTokenGenerator.generate(clientDetailsService, jwtTokenGenerator, BEARER_PREFIX);
        notValidToken = TestConstant.notValidToken;
    }

    @Nested
    @DisplayName("Блок тестов на *.getAuditRecordById() метод AuditController")
    class GetAuditRecordByIdMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть найденную аудит-запись для существующего ID")
        void getAuditRecordById_shouldReturnFoundAuditRecord_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/audits/{id}", existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            AuditReadDto readDto = objectMapper.readValue(strReturn, AuditReadDto.class);

            assertThat(existId).isEqualTo(readDto.id());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующего ID")
        void getAuditRecordById_shouldReturnNotFoundStatus_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/audits/{id}", nonExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getAuditRecordById_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/audits/{id}", existId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть список всех аудит-записей")
    void getAllAudits_shouldReturnAllAuditRecordsList_Test() {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/audits")
                        .header(HEADER_NAME, validJwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String strReturn = result.getResponse().getContentAsString();
        List<AuditReadDto> listFromResponse =
                objectMapper.readValue(strReturn, new TypeReference<List<AuditReadDto>>() {
                });

        assertThat(listFromResponse.size()).isGreaterThan(2);
    }

    @Nested
    @DisplayName("Блок тестов на *.getAuditByUserEmail() метод AuditController")
    class GetAuditByUserEmailMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть список всех аудит-записей для пользователя с заданным email")
        void getAuditByUserEmail_shouldReturnFoundRecordListByUserEmail_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/audits/")
                            .header(HEADER_NAME, validJwtToken)
                            .param("userEmail", existUserEmail))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            List<AuditReadDto> listFromResponse =
                    objectMapper.readValue(strReturn, new TypeReference<List<AuditReadDto>>() {
                    });

            assertThat(listFromResponse.size()).isGreaterThan(1);
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для пользователя с email не совершившего ни одного действия в системе")
        void getAuditByUserEmail_shouldReturnNotFoundStatus_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/audits/")
                            .header(HEADER_NAME, validJwtToken)
                            .param("userEmail", nonExistUserEmail))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getAuditByUserEmail_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/audits/")
                            .header(HEADER_NAME, notValidToken)
                            .param("userEmail", existUserEmail))
                    .andExpect(status().is4xxClientError());
        }
    }
}