package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/realtime")
@RequiredArgsConstructor
@Slf4j
public class RealtimeWeatherApiController {
    private final GeolocationService geolocationService;
    private final RealtimeWeatherService realtimeWeatherService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> getRealTimeWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);
        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
            return ResponseEntity.ok(entity2DTO(realtimeWeather));
        } catch (GeolocationException e) {
            log.error(e.getMessage());
           return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealTimeWeatherByLocationCode(@PathVariable(name = "locationCode") String locationCode) {
        RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
        return ResponseEntity.ok(entity2DTO(realtimeWeather));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealTimeWeatherByLocationCode(
            @PathVariable(name = "locationCode") String locationCode,
            @RequestBody @Valid RealtimeWeather realtimeWeatherRequest
    )
    {
        RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeatherRequest);
        RealTimeWeatherDTO dto = modelMapper.map(updatedRealtimeWeather, RealTimeWeatherDTO.class);
        return ResponseEntity.ok(dto);
    }

    private RealTimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather) {
        return modelMapper.map(realtimeWeather, RealTimeWeatherDTO.class);
    }


}
