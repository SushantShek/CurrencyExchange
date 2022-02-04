package com.marcura.currency.service;

import com.marcura.currency.config.PropertyMap;
import com.marcura.currency.exception.BadRequestException;
import com.marcura.currency.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class ExchangeCalculator {

    PropertyMap property;

    public ExchangeCalculator(PropertyMap property) {
        this.property = property;
    }

    /**
     * This method is responsible for calculation the exact exchange rate
     * based on the Spread provided which is in Properties files
     *
     * @param frm  String param of the country FROM which conversion is done
     * @param to   String param of the country TO which conversion is done
     * @param from the BigInt value of the frm current rate as per data in DB
     * @param to_b the BigInt value of the to current rate as per data in DB
     * @return final calculated Exchange Rate
     */
    public BigDecimal getExchangeSpread(String frm, String to, BigDecimal from, BigDecimal to_b) {
        if (!StringUtils.hasText(frm) || !StringUtils.hasText(to) || from == null || to_b == null) {
            log.error("Some unexpected values for from{} value{},to{} value{}", frm, to, from, to_b);
            throw new BadRequestException();
        }

        BigDecimal spreadFrom = new BigDecimal(property.getProperty(frm));
        BigDecimal spreadTo = new BigDecimal(property.getProperty(to));

        try {
            return (to_b.divide(from, 7, RoundingMode.HALF_UP))
                    .multiply((Constant.HUNDRED.subtract(spreadFrom.compareTo(spreadTo) > 0 ? spreadFrom : spreadTo))
                            .divide(Constant.HUNDRED, 7, RoundingMode.HALF_UP));
        } catch (ArithmeticException aex) {
            log.error("error while calculating rates " + aex);
            throw new BadRequestException();
        }
    }
}
