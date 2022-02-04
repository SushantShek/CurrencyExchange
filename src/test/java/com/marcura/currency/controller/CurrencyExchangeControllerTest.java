package com.marcura.currency.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.marcura.currency.domain.Response;
import com.marcura.currency.entity.Rates;
import com.marcura.currency.service.ExchangeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = CurrencyExchangeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CurrencyExchangeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    ExchangeService service;

    @Test
    void getExchangeById() throws Exception {
        Response response = new Response("AED", "USD", "2675890");
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(response);
        when(service.getExchangeRateByDate(any(), any(), any())).thenReturn(response);

        mvc.perform(get("/exchange?from=EUR&to=USD&date=2022-01-28")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("AED")))
                .andExpect(jsonPath("$", Matchers.aMapWithSize(3)));
    }

    @Test
    void postExchangeList() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String input = """
                {
                    "success": true,
                    "timestamp": 1643552343,
                    "base": "EUR",
                    "date": "2022-01-30",
                    "rates": {
                        "AED": 4.094738,
                        "AFN": 114.794492,
                        "ALL": 120.80368
                        }
                }
                """;
        Map<String, Double> doubleMap = new HashMap<>();
        doubleMap.put("AED", 2.034d);
        doubleMap.put("EUR", 1.012d);
        Map<String, Integer> integerMap = new HashMap<>();
        integerMap.put("AED", 2);
        integerMap.put("EUR", 3);

        Rates rates = new Rates("2022-02-02", doubleMap, integerMap);

        given(service.saveExchangeRate(any())).willReturn(rates);
        mvc.perform(post("/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2022-02-02")));
    }
}