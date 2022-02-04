package com.marcura.currency.controller;

import com.marcura.currency.domain.Response;
import com.marcura.currency.entity.Rates;
import com.marcura.currency.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CurrencyExchangeController {

    private ExchangeService service;

    @Autowired
    public CurrencyExchangeController(ExchangeService service) {
        this.service = service;
    }

    /**
     * Get currency exchange value for the set of currency code
     * added for the date provided
     *
     * Date is optional, if not provided, response will be based on the latest date
     * for which data is available in database,
     * if not available 404 response will be served
     *
     * @param from currency code
     * @param to currency code
     * @param date for which rate is requested
     * @return {@link Response} object as output
     */
    @GetMapping("/exchange")
    @ResponseBody
    public Response getExchangeByDate(@RequestParam String from, @RequestParam String to, @RequestParam(required = false) String date) {
        return service.getExchangeRateByDate(date, from, to);
    }

    /**
     * Additional API to manually upload exchange rates
     * if needed.
     * @param response object which is returned from fixer api
     * @return {@link Rates} list of all the rates saved
     * @throws IOException if there is issue with reading the input data
     */
    @PostMapping("/exchange")
    @ResponseBody
    public Rates postExchangeList(@RequestBody String response) throws IOException {
        return service.saveExchangeRate(response);
    }

    @PutMapping("/exchange")
    @ResponseBody
    public Rates putExchangeList(@RequestBody String response) throws IOException {
        return service.updateExchangeRates(response);
    }
}
