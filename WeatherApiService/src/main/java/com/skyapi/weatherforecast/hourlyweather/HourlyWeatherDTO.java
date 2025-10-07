package com.skyapi.weatherforecast.hourlyweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@JsonPropertyOrder({"hour_of_day", "temperature", "precipitation", "status"})
public class HourlyWeatherDTO {
    @JsonProperty("hour_of_day")
    private int hourOfDay;

    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 or 50 Celsius degree")
    private int temperature;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 degrees")
    private int precipitation;

    @NotBlank(message = "Status must not be empty")
    @Length(min = 3, max = 50, message = "Status must be in between 3 and 50")
    private String status;

    public HourlyWeatherDTO precipitation(int precipitation) {
        setPrecipitation(precipitation);
        return this;
    }

    public HourlyWeatherDTO status(String status) {
        setStatus(status);
        return this;
    }

    public HourlyWeatherDTO hourOfDay(int hour) {
        setHourOfDay(hour);
        return this;
    }

    public HourlyWeatherDTO temperature(int temp) {
        setTemperature(temp);
        return this;
    }

    @Override
    public String toString() {
        return "HourlyWeatherDTO{" +
                "hourOfDay=" + hourOfDay +
                ", temperature=" + temperature +
                ", precipitation=" + precipitation +
                ", status='" + status + '\'' +
                '}';
    }
}
