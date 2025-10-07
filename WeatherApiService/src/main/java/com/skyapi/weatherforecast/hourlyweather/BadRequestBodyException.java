package com.skyapi.weatherforecast.hourlyweather;

public class BadRequestBodyException extends Exception {
    public BadRequestBodyException(String message) {
        super(message);
    }
}
