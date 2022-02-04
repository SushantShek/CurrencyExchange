package com.marcura.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcura.currency.config.PropertyMap;
import com.marcura.currency.domain.Response;
import com.marcura.currency.entity.Rates;
import com.marcura.currency.exception.BadRequestException;
import com.marcura.currency.exception.ExchangeResourceNotFoundException;
import com.marcura.currency.repository.RatesRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.annotation.Retention;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ExchangeServiceTest {

    static final String REQUEST = """
             {
                        "success":true,
                        "timestamp":1643552343,
                        "base":"EUR",
                        "date":"2022-01-30",
                        "rates": {
                        "AED":4.094738,
                        "AFN":114.794492,
                        "ALL":120.80368
                }
            }
            """;

    @Mock
    PropertyMap prop;
    @Mock
    private RatesRepository ratesRepository;
    @Mock
    private ObjectMapper mapper;
    ExchangeService service;
    @Mock
    Rates rates;
    @Mock
    ExchangeCalculator exchangeCalculator;

    @BeforeEach
    void init() {
        service = new ExchangeService(ratesRepository, prop, new ObjectMapper());
    }


    @Test
    void saveExchangeRate() throws JsonProcessingException {
        Map<String, Double> doubleMap = new HashMap<>();
        Map<String, Integer> integerMap = new HashMap<>();

        Rates result = new Rates("2022-02-02", doubleMap, integerMap);
        when(ratesRepository.save(any(Rates.class))).thenReturn(result);
        Rates response = service.saveExchangeRate(REQUEST);
        assertNotNull(response);
        assertEquals("2022-02-02", response.getId());
    }

    @Test
    void saveExchangeRate_validate() throws JsonProcessingException {
        Map<String, Double> doubleMap = new HashMap<>();
        doubleMap.put("AED", 2.034d);
        Map<String, Integer> integerMap = new HashMap<>();
        integerMap.put("AED", 1);

        Rates result = new Rates("2022-02-02", doubleMap, integerMap);
        when(ratesRepository.save(any(Rates.class))).thenReturn(result);
        Rates response = service.saveExchangeRate(REQUEST);
        assertNotNull(response);
        assertEquals(1, doubleMap.size());
        assertEquals(1, integerMap.size());
    }

    @Test
    void saveExchangeRate_null_input_exception(){
        assertThrows(BadRequestException.class, () -> {
            service.saveExchangeRate(null);
        });
    }

    @Test
    void getExchangeRateByDate() {
        Map<String, Double> doubleMap = new HashMap<>();
        doubleMap.put("AED", 2.034d);
        doubleMap.put("EUR", 1.012d);
        Map<String, Integer> integerMap = new HashMap<>();
        integerMap.put("AED", 2);
        integerMap.put("EUR", 3);

        Rates result = new Rates("2022-02-02", doubleMap, integerMap);
        when(ratesRepository.findById(anyString())).thenReturn(result);
        when(exchangeCalculator.getExchangeSpread(anyString(),anyString(),any(BigDecimal.class),any(BigDecimal.class)))
                .thenReturn(new BigDecimal(1.000));
        when(prop.getProperty(anyString())).thenReturn("1.000");
        Response response = service.getExchangeRateByDate("2022-02-02","AED","EUR");
        assertNotNull(response);
        assertEquals(("0.49256638200000"), response.exchange().toString());
    }

    @Test
    void getExchangeRateByDate_no_db_entry_exception() {
        Map<String, Double> doubleMap = new HashMap<>();
        doubleMap.put("AED", 2.034d);
        doubleMap.put("EUR", 1.012d);
        Map<String, Integer> integerMap = new HashMap<>();
        integerMap.put("AED", 2);
        integerMap.put("EUR", 3);
        when(ratesRepository.findById(anyString())).thenReturn(null);
        assertThrows(ExchangeResourceNotFoundException.class, () -> {
            service.getExchangeRateByDate("2022-02-02","AED","EUR");
        });
    }

    @Test
    void getExchangeRateByDate_null_input_exception() {
        assertThrows(BadRequestException.class, () -> {
            service.getExchangeRateByDate("2022-02-02",null,"EUR");
        });
    }
}