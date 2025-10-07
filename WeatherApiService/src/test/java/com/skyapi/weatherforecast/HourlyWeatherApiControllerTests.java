package com.skyapi.weatherforecast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourlyweather.HourlyWeatherApiController;
import com.skyapi.weatherforecast.hourlyweather.HourlyWeatherDTO;
import com.skyapi.weatherforecast.hourlyweather.HourlyWeatherService;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HourlyWeatherApiController.class)
public class HourlyWeatherApiControllerTests {
    private static final String END_POINT_PATH = "/v1/hourly";
    private static final String X_CURRENT_HOUR = "X-Current-Hour";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HourlyWeatherService hourlyWeatherService;

    @MockitoBean
    private GeolocationService geolocationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
        when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
        when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);

        mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour", "9"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

   @Test
   public void testGetByIpShouldReturn204NoContent() throws Exception {
        int currentHour = 9;
        Location location = new Location().code("DELHI_IN");

        when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);

        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of());

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent())
                .andDo(print());
   }

   @Test
   public void testGetByIpShouldReturn200OK() throws Exception {
        int currentHour = 9;

        Location location = new Location();
        location.setLocationCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(13)
                .precipitation(60)
                .status("Sunny");

        when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andDo(print());

   }

   @Test
   public void testGetByCodeShouldReturn400BadRequest() throws Exception {
        String locationCode = "DELHI_IN";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andDo(print());

   }

   @Test
   public void testGetByCodeShouldReturn404NotFound() throws Exception {
        int currentHour = 11;
        String locationCode = "HCM_VN";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNotFound())
                .andDo(print());
   }

   @Test
   public void testGetByCodeShouldReturn204NoContent() throws Exception {
       int currentHour = 9;
       String locationCode = "DELHI_IN";
       String requestURI = END_POINT_PATH + "/" + locationCode;

       when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(List.of());

       mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
               .andExpect(status().isNoContent())
               .andDo(print());
   }

   @Test
   public void testGetByCodeShouldReturn200OK() throws Exception {
       int currentHour = 9;
        String locationCode = "DELHI_IN";
       String requestURI = END_POINT_PATH + "/" + locationCode;

       Location location = new Location();
       location.setLocationCode(locationCode);
       location.setCityName("Delhi");
       location.setRegionName("Delhi");
       location.setCountryCode("IN");
       location.setCountryName("India");

       HourlyWeather forecast1 = new HourlyWeather()
               .location(location)
               .hourOfDay(10)
               .temperature(13)
               .precipitation(70)
               .status("Cloudy");

       HourlyWeather forecast2 = new HourlyWeather()
               .location(location)
               .hourOfDay(11)
               .temperature(13)
               .precipitation(60)
               .status("Sunny");

       when(hourlyWeatherService.getByLocationCode(Mockito.anyString(), Mockito.anyInt())).thenReturn(List.of(forecast1, forecast2));



       mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, currentHour))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json"))
               .andExpect(jsonPath("$.location", is(location.toString())))
               .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
               .andDo(print());


   }

   @Test
   public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";
        List<HourlyWeatherDTO> listDTO = Collections.emptyList();

        String bodyContent = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty")))
                .andDo(print());

   }

   @Test
   public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";

        HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
                .hourOfDay(10)
                .temperature(133)
                .precipitation(700)
                .status("Cloudy");

        HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        List<HourlyWeatherDTO> listDTO = List.of(dto1, dto2);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
   }

   @Test
   public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/"  + locationCode;

        HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        List<HourlyWeatherDTO> listDTO = List.of(dto1);

        when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenThrow(LocationNotFoundException.class);

        String requestBody = objectMapper.writeValueAsString(listDTO);

       mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
               .andExpect(status().isNotFound())
               .andDo(print());

   }

   @Test
   public void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        Location location = new Location();
        location.setLocationCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        List<HourlyWeatherDTO> lisDTO = List.of(dto1, dto2);

        List<HourlyWeather> hourlyForecast = List.of(forecast1, forecast2);

        String requestBody = objectMapper.writeValueAsString(lisDTO);

        when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenReturn(hourlyForecast);

        mockMvc.perform(put(requestURI).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location", is(location.toString())))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andDo(print());

   }










}
