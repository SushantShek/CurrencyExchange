package com.marcura.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.marcura.currency.utils.Constant;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@NoArgsConstructor
public class RatesScheduler {

    private ExchangeService service;

    public RatesScheduler(ExchangeService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 5 0 * * ?", zone = Constant.TIME_ZONE)
    public void scheduleTaskUsingCronExpression() {
        try {
            service.saveExchangeRate(getRatesFormFixer());
        } catch (JsonProcessingException jEx) {
            log.error("Json processing exception for fixer" + jEx);
        }
    }

    private String getRatesFormFixer() {
        final String uri = Constant.FIXER_URI;

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }
}
