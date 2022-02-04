package com.marcura.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcura.currency.config.PropertyMap;
import com.marcura.currency.domain.Response;
import com.marcura.currency.entity.Rates;
import com.marcura.currency.exception.BadRequestException;
import com.marcura.currency.exception.ExchangeResourceNotFoundException;
import com.marcura.currency.repository.RatesRepository;
import com.marcura.currency.utils.Constant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExchangeService {

    private PropertyMap prop;
    private RatesRepository ratesRepository;
    private ObjectMapper mapper;


    public ExchangeService( RatesRepository ratesRepository, PropertyMap prop, ObjectMapper mapper) {
        this.ratesRepository = ratesRepository;
        this.prop = prop;
        this.mapper = mapper;
    }

    /**
     * Update rates for a provided country
     *
     * @param request {@link Rates} String object to be updated
     * @return {@link Rates} if saved successfully
     */
    public Rates saveExchangeRate(String request) throws JsonProcessingException {
        if(!StringUtils.hasText(request)){
            throw new BadRequestException();
        }
        java.util.HashMap<String, Object> result = mapper.readValue(request, HashMap.class);
        Object s = result.get(Constant.RATES_KEY);
        Map<String, Object> ex = mapper.readValue(mapper.writeValueAsString(s), HashMap.class);

        Map<String, Double> newMap = ex.entrySet().stream()
                .filter(e -> !e.getValue().equals(1))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (Double) e.getValue()));
        // To check for base currency provided by API
        newMap.put((String) result.get(Constant.BASE_CURRENCY), 1.000d);

        Map<String, Integer> countermap = new HashMap<>();
        newMap.keySet().stream().forEach(k -> countermap.put(k, 0));

        Rates r = new Rates((String) result.get("date"), newMap, countermap);
        return ratesRepository.save(r);
    }
    /**
     * Update all the rates from fixer
     * into the database
     *
     * @param rates String {@link Rates}
     * @return {@link Rates} object if saved successfully
     */
    public Rates updateExchangeRates(String rates) throws JsonProcessingException {
        if(!StringUtils.hasText(rates)){
            throw new BadRequestException();
        }
        java.util.HashMap<String, Object> rateObjectMap = mapper.readValue(rates, HashMap.class);
        Map<String, Double> mapToUpdate = mapper.readValue(mapper.writeValueAsString(rateObjectMap.get(Constant.RATES_KEY)), HashMap.class);
        Rates r = new Rates((String) rateObjectMap.get("date"), mapToUpdate, null);
        return ratesRepository.save(r);
    }

    /**
     * Get the exchange rates of 2 countries
     * on a given date
     * If no date is provided it gets the most recent rates
     *
     * @param date of the exchanges
     * @param from which currency to be converted
     * @param to   which currency is been converted
     * @return {@link Response} generic response object
     */
    public Response getExchangeRateByDate(String date, String from, String to) {
        if (ObjectUtils.isEmpty(from) || ObjectUtils.isEmpty(to)) {
            throw new BadRequestException();
        }
        if (!StringUtils.hasText(date)) {
            date = findMostRecent();
        }
        Rates rateMap = ratesRepository.findById(date);
        if (ObjectUtils.isEmpty(rateMap)) {
            throw new ExchangeResourceNotFoundException("No value found for provided countries");
        }

        BigDecimal frm_big = new BigDecimal(rateMap.getItemPriceMap().get(from));
        BigDecimal to_big = new BigDecimal(rateMap.getItemPriceMap().get(to));

        ExchangeCalculator exchangeCalculator = new ExchangeCalculator(prop);
        updateCounter(rateMap, from, to);
        return new Response(from, to, exchangeCalculator.getExchangeSpread(from, to, frm_big, to_big).toString());
    }

    /**
     * Updates the current counter of the
     * countries which are been called
     * @param rateMap {@link Rates} object
     * @param frm the currency been converted
     * @param to the currency to converted
     */
    private void updateCounter(Rates rateMap, String frm, String to) {
        Map<String, Integer> counter = rateMap.getCounter();
        counter.put(frm, counter.get(frm) + 1);
        counter.put(to, counter.get(to) + 1);

        ratesRepository.saveAndFlush(rateMap);
    }

    /**
     * Helper method to find the most recent date
     * in case of user not providing the date
     * for which rates are needed
     *
     * @return most recent date from Db
     */
    private String findMostRecent() {
        Rates currencyRates = ratesRepository.findFirstByOrderByIdDesc();
        if (ObjectUtils.isEmpty(currencyRates)) {
            throw new ExchangeResourceNotFoundException("Seems like the database is empty..");
        }
        return currencyRates.getId();
    }
}
