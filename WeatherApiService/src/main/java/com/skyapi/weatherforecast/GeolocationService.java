package com.skyapi.weatherforecast;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class GeolocationService {
    private final static String DBPath = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
    private final IP2Location ipLocator = new IP2Location();

    public GeolocationService() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DBPath);
            byte[] data = inputStream.readAllBytes();
            ipLocator.Open(data);
            inputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public Location getLocation(String ipAddress)  {
        try {
            IPResult result =  ipLocator.IPQuery(ipAddress);
            if(!"OK".equals(result.getStatus())) {
                throw new GeolocationException("Geolocation failed with status: " + result.getStatus());
            }
            return new Location(result.getCity(), result.getRegion(), result.getCountryLong(), result.getCountryShort());

        } catch (IOException e) {
           throw new GeolocationException("Error querying IP database", e);
        }

    }
}
