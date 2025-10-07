package com.skyapi.weatherforecast;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourlyweather.HourlyWeatherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class HourlyWeatherRepositoryTests {
    private final HourlyWeatherRepository repo;

    @Autowired
    public HourlyWeatherRepositoryTests(HourlyWeatherRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testAdd() {
        String locationCode = "DELHI_IN";
        int hourOfDay = 13;

        Location location = new Location().code(locationCode);

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(hourOfDay)
                .temperature(13)
                .precipitation(70)
                .status("Sunny");

        HourlyWeather updatedForecast = repo.save(forecast1);

        assertThat(updatedForecast.getId().getLocation().getLocationCode()).isEqualTo(locationCode);
        assertThat(updatedForecast.getId().getHourOfDay()).isEqualTo(hourOfDay);
    }

    @Test
    public void testDelete() {
        Location location = new Location().code("DELHI_IN");
        HourlyWeatherId id = new HourlyWeatherId(10, location);
        repo.deleteById(id);

        Optional<HourlyWeather> result = repo.findById(id);

        assertThat(result).isNotPresent();
    }

    @Test
    public void testFindByLocationCodeFound() {
        String locationCode = "DELHI_IN";
        int currentHour = 10;

        List<HourlyWeather> hourlyForecasts = repo.findByLocationCode(locationCode, currentHour);

        assertThat(hourlyForecasts).isNotEmpty();
    }

    @Test
    public void testFindByLocationCodeNotFound() {
        String locationCode = "DELHIIN";
        int currentHour = 10;

        List<HourlyWeather> hourlyForecasts = repo.findByLocationCode(locationCode, currentHour);

        assertThat(hourlyForecasts).isEmpty();
    }




}
