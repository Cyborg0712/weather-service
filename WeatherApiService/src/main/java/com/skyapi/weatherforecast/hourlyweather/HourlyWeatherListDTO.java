package com.skyapi.weatherforecast.hourlyweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class HourlyWeatherListDTO {
    private String location;

    @JsonProperty("hourly_forecast")
    private List<HourlyWeatherDTO> hourlyForecast = new ArrayList<>();

    public void addWeatherHourlyDTO(HourlyWeatherDTO hourlyWeatherDTO) {
        this.hourlyForecast.add(hourlyWeatherDTO);
    }
}
