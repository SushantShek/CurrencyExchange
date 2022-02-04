package com.marcura.currency.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ExchangeResourceNotFoundException extends RuntimeException {
    public ExchangeResourceNotFoundException() {
        super();
    }

    public ExchangeResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeResourceNotFoundException(String message) {
        super(message);
    }

    public ExchangeResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}
