package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@RequiredArgsConstructor
@Validated
public class HourlyWeatherApiController {
    private final HourlyWeatherService hourlyWeatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request) {
        String IpAddress = CommonUtility.getIPAddress(request);
        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            Location locationFromIP = geolocationService.getLocation(IpAddress);
            List<HourlyWeather> hourlyForecast =  hourlyWeatherService.getByLocation(locationFromIP, currentHour);

            if(hourlyForecast.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntity2DTO(locationFromIP, hourlyForecast ));

        } catch (NumberFormatException e) {
            throw new GeolocationException("The value of X-Current-Hour is invalid. Please try again");
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable(name = "locationCode") String locationCode, HttpServletRequest request) {
        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocationCode(locationCode, currentHour);

            if(hourlyForecast.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(listEntity2DTO(hourlyForecast.getFirst().getId().getLocation(), hourlyForecast));

        }catch (NumberFormatException e) {
           throw new GeolocationException("The value of X-Current-Hour is invalid. Please try again");
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(
            @PathVariable("locationCode") String locationCode,
            @RequestBody @Valid List<HourlyWeatherDTO> listDTO
    ) throws BadRequestBodyException {
        if(listDTO.isEmpty()) {
          throw new BadRequestBodyException("Hourly forecast data cannot be empty");
        }

        List<HourlyWeather> listHourlyWeather = listDTO2ListEntity(listDTO);

        listHourlyWeather.forEach(System.out::println);

        List<HourlyWeather> updateHourlyWeather = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);
        return ResponseEntity.ok(listEntity2DTO(updateHourlyWeather.getFirst().getId().getLocation(), updateHourlyWeather));

    }


    private List<HourlyWeather> listDTO2ListEntity(List<HourlyWeatherDTO> listDTO) {
        List<HourlyWeather> listEntity = new ArrayList<>();

        listDTO.forEach(dto -> {
            listEntity.add(modelMapper.map(dto, HourlyWeather.class));
        });

        return listEntity;
    }

    private HourlyWeatherListDTO listEntity2DTO(Location location, List<HourlyWeather> hourlyForecast) {
        HourlyWeatherListDTO resultDTO = new HourlyWeatherListDTO();
        resultDTO.setLocation(location.toString());

        hourlyForecast.forEach(hourlyWeather -> {
             HourlyWeatherDTO hourlyWeatherDTO = modelMapper.map(hourlyWeather, HourlyWeatherDTO.class);
             resultDTO.addWeatherHourlyDTO(hourlyWeatherDTO);
        });

        return resultDTO;
    }


}
