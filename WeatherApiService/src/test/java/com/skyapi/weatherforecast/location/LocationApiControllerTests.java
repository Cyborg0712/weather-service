package com.skyapi.weatherforecast.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {
    private static final String END_POINT_PATH = "/v1/locations";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    LocationService locationService;

    @Test
    public void testAddShouldReturn400BadRequest() throws Exception {
        Location location = new Location();

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print())
        ;
    }

    @Test
    public void testAddShouldReturn201Created() throws Exception {
        Location location = new Location();
        location.setLocationCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        LocationDTO dto = new LocationDTO();
        dto.setLocationCode(location.getLocationCode());
        dto.setCityName(location.getCityName());
        dto.setRegionName(location.getRegionName());
        dto.setCountryCode(location.getCountryCode());
        dto.setCountryName(location.getCountryName());
        dto.setEnabled(location.isEnabled());

        when(locationService.add(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(dto);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location_code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name",is("New York City")))
                .andExpect(header().string("Location", "/v1/locations/NYC_USA"))
                .andDo(print());

    }

    @Test
    public void testListShouldReturn204NoContent() throws Exception {
        when(locationService.list()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testListShouldReturn200OK() throws Exception {
            Location location1 = new Location();
            location1.setLocationCode("NYC_USA");
            location1.setCityName("New York City");
            location1.setRegionName("New York");
            location1.setCountryCode("US");
            location1.setCountryName("United States of America");
            location1.setEnabled(true);

            Location location2 = new Location();
            location2.setLocationCode("LACA_USA");
            location2.setCityName("Los Angeles");
            location2.setRegionName("California");
            location2.setCountryCode("US");
            location2.setCountryName("United States of America");
            location2.setEnabled(true);

             Location location3 = new Location();
             location3.setLocationCode("DELHI_IN");
             location3.setCityName("New Delhi");
             location3.setRegionName("Delhi");
             location3.setCountryCode("IN");
             location3.setCountryName("India");
             location3.setEnabled(true);
             location3.setTrashed(true);

             when(locationService.list()).thenReturn(List.of(location1, location2, location3));

             mockMvc.perform(get(END_POINT_PATH))
                     .andExpect(status().isOk())
                     .andExpect(content().contentType("application/json"))
                     .andExpect(jsonPath("$[0].location_code" , is("NYC_USA")))
                     .andExpect(jsonPath("$[0].city_name", is("New York City")))
                     .andExpect(jsonPath("$[1].location_code", is("LACA_USA")))
                     .andExpect(jsonPath("$[1].city_name", is("Los Angeles")))
                     .andDo(print());
    }

    @Test
    public void testGetShouldReturn405MethodNotAllowed() throws Exception {
        String requestURI = END_POINT_PATH + "/ABCDEF";

        mockMvc.perform(post(requestURI))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn404NotFound() throws Exception {
        String locationCode = "ABCDEF";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);

        when(locationService.get(locationCode)).thenThrow(ex);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn200OK() throws Exception {
        String code = "LACA_USA";
        String requestURI = END_POINT_PATH + "/"+ code;

        Location location = new Location();
        location.setLocationCode(code);
        location.setCityName("Los Angeles");
        location.setRegionName("California");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        when(locationService.get(code)).thenReturn(location);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location_code", is(code)))
                .andExpect(jsonPath("$.city_name", is("Los Angeles")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        LocationDTO location = new LocationDTO();
        location.setLocationCode("LACA_USA");
        location.setCityName("Los Angeles");
        location.setRegionName("California");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        LocationNotFoundException ex = new LocationNotFoundException(location.getLocationCode());

        when(locationService.update(Mockito.any())).thenThrow(ex);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception {
        LocationDTO location = new LocationDTO();
        location.setCityName("Los Angeles");
        location.setRegionName("California");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        Location location = new Location();
        location.setLocationCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        LocationDTO dto = new LocationDTO();
        dto.setLocationCode(location.getLocationCode());
        dto.setCityName(location.getCityName());
        dto.setRegionName(location.getRegionName());
        dto.setCountryCode(location.getCountryCode());
        dto.setCountryName(location.getCountryName());
        dto.setEnabled(location.isEnabled());

        when(locationService.update(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(dto);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location_code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andDo(print());

    }


    @Test
    public void testDeleteShouldReturn404NotFound() throws Exception {
        String code = "LACA_USA";
        String requestURI = END_POINT_PATH + "/" + code;

        LocationNotFoundException ex = new LocationNotFoundException(code);

        when(locationService.get(code)).thenThrow(ex);

        Mockito.doThrow(ex).when(locationService).delete(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());

    }

    @Test
    public void testDeleteShouldReturn204NoContent() throws Exception {
        String code = "LACA_USA";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doNothing().when(locationService).delete(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testValidateRequestBodyLocationCodeNotNull() throws Exception {
        LocationDTO location = new LocationDTO();
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors[0]", is("Location code cannot be null")))
                .andDo(print());

    }

    @Test
    public void testValidateRequestBodyLocationCodeLength() throws Exception {
        LocationDTO location = new LocationDTO();
        location.setLocationCode("");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors[0]", is("Location code must have 3-12 characters")))
                .andDo(print());
    }

    @Test
    public void testValidateRequestsBodyAllFieldsInvalid() throws Exception {
        LocationDTO location = new LocationDTO();
        location.setRegionName("");

        String bodyContent = mapper.writeValueAsString(location);

        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("Location code cannot be null");
        assertThat(responseBody).contains("City name cannot be null");
        assertThat(responseBody).contains("Region name must have 3-128 characters");
        assertThat(responseBody).contains("Country name cannot be null");
        assertThat(responseBody).contains("Country code cannot be null");
    }













}
