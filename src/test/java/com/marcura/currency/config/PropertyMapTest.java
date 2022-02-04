package com.marcura.currency.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PropertyMapTest {

    @InjectMocks
    PropertyMap propertyMap;

    @Mock
    Environment env;

    @Test
    void getProperty() {
        when(env.containsProperty(Mockito.anyString())).thenReturn(true);
        when(env.getRequiredProperty(Mockito.anyString())).thenReturn("1.00");
       String response = propertyMap.getProperty("EUR");
       assertEquals("1.00", response);
    }

    @Test
    void getProperty_invalid(){
        String response = propertyMap.getProperty("EUR");
        assertEquals(null, response);

    }
}