package com.skyapi.weatherforecast.common;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class HourlyWeatherId implements Serializable {
    private int hourOfDay;

    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;

    public HourlyWeatherId() {
    }

    public HourlyWeatherId(int hourOfDay, Location location) {
        this.hourOfDay = hourOfDay;
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HourlyWeatherId that = (HourlyWeatherId) o;
        return hourOfDay == that.hourOfDay && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hourOfDay, location);
    }
}
