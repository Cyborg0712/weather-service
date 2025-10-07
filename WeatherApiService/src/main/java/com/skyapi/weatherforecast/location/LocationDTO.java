package com.skyapi.weatherforecast.location;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@JsonPropertyOrder({"location_code", "city_name", "region_name", "country_code", "country_name","enabled"})
public class LocationDTO{
    @NotNull(message = "Location code cannot be null")
    @Length(min = 3, max = 12, message = "Location code must have 3-12 characters")
    @JsonProperty("location_code")
    private String locationCode;

    @NotNull(message = "City name cannot be null")
    @Length(min = 3, max = 128, message = "City name must have 3-128 characters")
    @JsonProperty("city_name")
    private String cityName;

    @Length(min = 3, max = 128, message = "Region name must have 3-128 characters")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("region_name")
    private String regionName;

    @NotNull(message = "Country code cannot be null")
    @Length(min = 2, max = 2, message = "Country code must have 2 characters")
    @JsonProperty("country_code")
    private String countryCode;

    @NotNull(message = "Country name cannot be null")
    @Length(min = 3, max = 128, message = "Country name must have 3-64 characters")
    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("enabled")
    private boolean enabled;
}
