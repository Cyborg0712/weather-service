package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class RealtimeWeatherService {
    private final RealTimeWeatherRepository realTimeWeatherRepository;
    private final LocationRepository locationRepository;

    public RealtimeWeather getByLocation(Location location){
            String countryCode = location.getCountryCode();
            String cityName = location.getCityName();

            RealtimeWeather realtimeWeather = realTimeWeatherRepository.findByCountryCodeAndCity(countryCode, cityName);

            if(realtimeWeather == null) {
                throw new LocationNotFoundException(countryCode, cityName);
            }
            return realtimeWeather;
    }

    public RealtimeWeather getByLocationCode(String locationCode){
        RealtimeWeather realtimeWeather = realTimeWeatherRepository.findByLocationCode(locationCode);
        if(realtimeWeather == null) {
            throw new LocationNotFoundException(locationCode);
        }
        return realtimeWeather;
    }

    public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather) {
            Location location = locationRepository.findByCode(locationCode);
            if(location == null) {
                throw new LocationNotFoundException(locationCode);
            }

            RealtimeWeather existingRealtimeWeather = location.getRealtimeWeather();
            if(existingRealtimeWeather == null) {
                realtimeWeather.setLocation(location);
                realtimeWeather.setLastUpdated(new Date());
                return realTimeWeatherRepository.save(realtimeWeather);
            }
            else {
                existingRealtimeWeather.setTemperature(realtimeWeather.getTemperature());
                existingRealtimeWeather.setHumidity(realtimeWeather.getHumidity());
                existingRealtimeWeather.setPrecipitation(realtimeWeather.getPrecipitation());
                existingRealtimeWeather.setStatus(realtimeWeather.getStatus());
                existingRealtimeWeather.setWindSpeed(realtimeWeather.getWindSpeed());
                existingRealtimeWeather.setLastUpdated(new Date());
                return realTimeWeatherRepository.save(existingRealtimeWeather);
            }

    }


}
