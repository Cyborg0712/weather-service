package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "realtime_weather")
@Getter
@Setter
public class RealtimeWeather {
    @Id
    @Column(name = "location_code")
    @JsonIgnore
    private String locationCode;

    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 or 50 Celsius degree")
    private int temperature;

    @Range(min = 0, max = 100, message = "Humidity must be in the range of 0 to 100 degrees")
    private int humidity;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 degrees")
    private int precipitation;

    @JsonProperty("wind_speed")
    @Range(min = 0, max = 200, message = "Wind speed must be in the range of 0 to 200 km/h")
    private int windSpeed;

    @Column(length = 50)
    @NotBlank(message = "Status must not be empty")
    @Length(min = 3, max = 50, message = "Status must be in between 3 and 50")
    private String status;

    @OneToOne
    @JoinColumn(name = "location_code")
    @MapsId
    @JsonIgnore
    private Location location;

    @JsonProperty("last_updated")
    private Date lastUpdated;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RealtimeWeather that = (RealtimeWeather) o;
        return Objects.equals(locationCode, that.locationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(locationCode);
    }
}
