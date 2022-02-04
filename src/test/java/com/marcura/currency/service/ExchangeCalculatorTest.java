package com.marcura.currency.service;

import com.marcura.currency.config.PropertyMap;
import com.marcura.currency.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ExchangeCalculatorTest {

    @InjectMocks
    ExchangeCalculator calculator;

    @Mock
    PropertyMap property;


    @Test
    void getExchangeSpread() {
        String from = "EUR", to = "USD";
        BigDecimal bigDecimal = new BigDecimal("1.000");
        when(property.getProperty(anyString())).thenReturn("1.000");
        BigDecimal result =calculator.getExchangeSpread(from, to, bigDecimal, bigDecimal);
        assertEquals("0.99000000000000",result.toString());
    }

    @Test
    void getExchangeSpread_missing_from(){
        String from = null, to = "USD";
        BigDecimal bigDecimal = new BigDecimal("1.000");
        assertThrows(BadRequestException.class, () -> {
            calculator.getExchangeSpread(from, to, bigDecimal, bigDecimal);
        });
    }

    @Test
    void getExchangeSpread_missing_spread(){
        String from = "EUR", to = "USD";
        BigDecimal bigDecimal = new BigDecimal("1.000");
        assertThrows(BadRequestException.class, () -> {
            calculator.getExchangeSpread(from, to, bigDecimal, null);
        });
    }
 }