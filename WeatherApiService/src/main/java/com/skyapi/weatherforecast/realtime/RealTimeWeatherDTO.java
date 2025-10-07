package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RealTimeWeatherDTO {
    private String location;

    private int temperature;
    private int humidity;
    private int precipitation;

    @JsonProperty("wind_speed")
    private int windSpeed;

    @Column(length = 50)
    private String status;

    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date lastUpdated;

}
