package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
public class LocationApiController {
    private final LocationService locationService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO dto) {
        Location newLocation = locationService.add(dto2Entity(dto));
        URI uri = URI.create("/v1/locations/" + newLocation.getLocationCode());
        return ResponseEntity.created(uri).body(entity2DTO(newLocation));
    }

    @GetMapping
    public ResponseEntity<?> listLocations() {
        List<Location> locations = locationService.list();
        if(locations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(listEntity2ListDTO(locations));
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getLocation(@PathVariable(name = "code") String code)  {
        Location location = locationService.get(code);
        return ResponseEntity.ok(entity2DTO(location));
    }

    @PutMapping
    public ResponseEntity<?> updateLocation(@RequestBody @Valid LocationDTO dto)  {
            Location updateLocation = locationService.update(dto2Entity(dto));
            return ResponseEntity.ok(entity2DTO(updateLocation));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable(name = "code") String code) {
            locationService.delete(code);
            return ResponseEntity.noContent().build();
    }

    private LocationDTO entity2DTO(Location location) {
        return modelMapper.map(location, LocationDTO.class);
    }

    private Location dto2Entity(LocationDTO locationDTO) {
        return modelMapper.map(locationDTO, Location.class);
    }

    private List<LocationDTO> listEntity2ListDTO(List<Location> locations) {
        return  locations.stream().map(location -> modelMapper.map(location, LocationDTO.class)).toList();
    }








}
