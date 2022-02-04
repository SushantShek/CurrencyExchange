package com.marcura.currency.config;

import com.marcura.currency.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class PropertyMap {

    @Autowired
    private Environment env;

    public String getProperty(String pPropertyKey) {
        log.info("getting property for key {}", pPropertyKey);
        if (env.containsProperty(Constant.PROPERTY_BASE + pPropertyKey))
            return env.getRequiredProperty(Constant.PROPERTY_BASE + pPropertyKey);
        else
            return env.getRequiredProperty(Constant.PROPERTY_DEFAULT);
    }
}
