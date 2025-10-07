package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;

    public Location add(Location location) {
        return locationRepository.save(location);
    }

    public List<Location> list() {
        return locationRepository.findUntrashed();
    }

    public Location get(String code)  {
        Location location = locationRepository.findByCode(code);
        if(location == null) {
            throw new  LocationNotFoundException(code);
        }
        return location;
    }

    public Location update(Location locationInRequest)  {
        String code = locationInRequest.getLocationCode();

        Location locationInDB = locationRepository.findByCode(code);

        if(locationInDB == null) {
            throw new LocationNotFoundException(code);
        }

        locationInDB.setCityName(locationInRequest.getCityName());
        locationInDB.setRegionName(locationInRequest.getRegionName());
        locationInDB.setCountryCode(locationInRequest.getCountryCode());
        locationInDB.setCountryName(locationInRequest.getCountryName());
        locationInDB.setEnabled(locationInRequest.isEnabled());

        return locationRepository.save(locationInDB);
    }

    public void delete(String code)  {
        Location location = locationRepository.findByCode(code);
        if (location == null) {
            throw new LocationNotFoundException(code);
        }
        locationRepository.trashByCode(code);
    }
}
