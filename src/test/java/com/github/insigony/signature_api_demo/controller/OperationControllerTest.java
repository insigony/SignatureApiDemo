package com.github.insigony.signature_api_demo.controller;

import com.github.insigony.signature_api_demo.config.AuthProperties;
import com.github.insigony.signature_api_demo.config.SignatureProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = OperationController.class)
class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignatureProperties signatureProperties;

    @MockBean
    private AuthProperties authProperties;

    @BeforeEach
    void setUp() {
        when(signatureProperties.getSecret()).thenReturn("signing-secret");
        when(authProperties.getToken()).thenReturn("super-secret-token");
    }

    @Test
    void shouldReturnSignatureForValidRequest() throws Exception {
        mockMvc.perform(post("/operations/123")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name1", "value1")
                        .param("name2", "value2")
                        .header("Token", "super-secret-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.result[0].signature").exists());
    }
}