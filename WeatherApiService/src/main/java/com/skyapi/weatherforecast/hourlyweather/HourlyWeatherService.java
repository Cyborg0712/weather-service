package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HourlyWeatherService {
    private final HourlyWeatherRepository hourlyWeatherRepository;
    private final LocationRepository locationRepository;

    public List<HourlyWeather> getByLocation(Location location, int currentHour) {
            String countryCode = location.getCountryCode();
            String cityName = location.getCityName();

            Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
            if(locationInDB == null) {
                throw new LocationNotFoundException(countryCode, cityName);
            }

            return hourlyWeatherRepository.findByLocationCode(locationInDB.getLocationCode(), currentHour);
    }

    public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour) {
           Location locationInDB = locationRepository.findByCode(locationCode);

           if(locationInDB == null) {
               throw new LocationNotFoundException(locationCode);
           }
           return hourlyWeatherRepository.findByLocationCode(locationInDB.getLocationCode(), currentHour);
    }

    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyForecastInRequest){
        Location location = locationRepository.findByCode(locationCode);
        if(location == null) {
            throw new LocationNotFoundException(locationCode);
        }

        hourlyForecastInRequest.forEach(forecast -> forecast.getId().setLocation(location));

        List<HourlyWeather> hourlyWeatherInDB = location.getListHourlyWeather();
        List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();

         for (HourlyWeather item: hourlyWeatherInDB) {
            if(!hourlyForecastInRequest.contains(item)){
               hourlyWeatherToBeRemoved.add(item.getShallowCopy());
            }
        }

         hourlyWeatherInDB.removeAll(hourlyWeatherToBeRemoved);


        return (List<HourlyWeather>)hourlyWeatherRepository.saveAll(hourlyForecastInRequest);
    }
}
