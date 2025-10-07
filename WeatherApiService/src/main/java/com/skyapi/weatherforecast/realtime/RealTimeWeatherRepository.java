package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.RealtimeWeather;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealTimeWeatherRepository extends CrudRepository<RealtimeWeather, String> {
    @Query("SELECT r FROM RealtimeWeather r WHERE r.location.countryCode=?1 AND r.location.cityName=?2")
    RealtimeWeather findByCountryCodeAndCity(String countryCode, String city);

    @Query("SELECT r FROM RealtimeWeather r WHERE r.locationCode=?1 AND r.location.trashed=false")
    RealtimeWeather findByLocationCode(String locationCode);
}
