package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class LocationRepositoryTests {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationRepositoryTests(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Test
    public void testAddSuccess() {
        Location location = new Location();
        location.setLocationCode("MBMH_IN");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        Location savedLocation = locationRepository.save(location);

        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getLocationCode()).isEqualTo("MBMH_IN");
    }

    @Test
    public void testListSuccess() {
        List<Location> locations = locationRepository.findUntrashed();
        assertThat(locations).isNotEmpty();
        locations.forEach(System.out::println);
    }

    @Test
    public void testGetNotFound() {
        String code = "ABCD";
        Location location = locationRepository.findByCode(code);
        assertThat(location).isNull();
    }

    @Test
    public void testGetFound() {
        String code = "DELHI_IN";
        Location location = locationRepository.findByCode(code);
        assertThat(location).isNotNull();
        assertThat(location.getLocationCode()).isEqualTo(code);
    }

    @Test
    public void testTrashSuccess() {
        String code = "LACA_USA";
        locationRepository.trashByCode(code);

        Location location = locationRepository.findByCode(code);

        assertThat(location).isNull();
    }

    @Test
    public void testAddRealTimeWeatherData() {
        String code = "NYC_USA";
        Location location =locationRepository.findByCode(code);

        RealtimeWeather realtimeWeather = location.getRealtimeWeather();

        if(realtimeWeather == null) {
            realtimeWeather = new RealtimeWeather();
            realtimeWeather.setLocation(location);
            location.setRealtimeWeather(realtimeWeather);
        }

        realtimeWeather.setTemperature(-1);
        realtimeWeather.setHumidity(30);
        realtimeWeather.setPrecipitation(40);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(15);
        realtimeWeather.setLastUpdated(new Date());

        Location updatedLocation = locationRepository.save(location);
        assertThat(updatedLocation.getRealtimeWeather().getLocationCode()).isEqualTo(code);
    }

    @Test
    public void testAddHourlyWeatherData() {
            Location location= locationRepository.findById("MBMH_IN").orElseThrow();

            List<HourlyWeather> listHourlyWeather = location.getListHourlyWeather();

            HourlyWeather  forecast1 = new HourlyWeather().id(location, 10)
                    .temperature(15)
                    .precipitation(40)
                    .status("Sunny");

            HourlyWeather forecast2 = new HourlyWeather()
                    .location(location)
                    .hourOfDay(11)
                    .temperature(16)
                    .precipitation(50)
                    .status("Cloudy");

            listHourlyWeather.add(forecast1);
            listHourlyWeather.add(forecast2);

            Location updatedLocation = locationRepository.save(location);

            assertThat(updatedLocation.getListHourlyWeather()).isNotEmpty();
    }


    @Test
    public void testFindByCountryAndCityNotFound() {
        String countryCode = "US";
        String cityName = "Los Angeles";

        Location location = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

        assertThat(location).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCiyNameFound() {
            String countryCode = "IN";
            String cityName = "Delhi";

            Location location = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

            assertThat(location).isNotNull();
            assertThat(location.getCountryCode()).isEqualTo(countryCode);
            assertThat(location.getCityName()).isEqualTo(cityName);
    }










}
