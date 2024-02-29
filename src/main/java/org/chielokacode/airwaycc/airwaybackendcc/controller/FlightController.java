package org.chielokacode.airwaycc.airwaybackendcc.controller;


import org.chielokacode.airwaycc.airwaybackendcc.dto.AddFlightDto;
import org.chielokacode.airwaycc.airwaybackendcc.dto.FlightSearchDto;
import org.chielokacode.airwaycc.airwaybackendcc.exception.AirlineNotFoundException;
import org.chielokacode.airwaycc.airwaybackendcc.exception.AirportNotFoundException;
import org.chielokacode.airwaycc.airwaybackendcc.exception.FlightNotFoundException;
import org.chielokacode.airwaycc.airwaybackendcc.model.Airport;
import org.chielokacode.airwaycc.airwaybackendcc.model.Flight;
import org.chielokacode.airwaycc.airwaybackendcc.serviceImpl.FlightServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {

    private final FlightServiceImpl flightService;

    @Autowired
    public FlightController(FlightServiceImpl flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/add-flight")
    private ResponseEntity<String> addFlight(@RequestBody AddFlightDto flightDto) throws AirportNotFoundException, AirlineNotFoundException {
        String response = flightService.addNewFlight(flightDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/update-flight/{id}")
    private ResponseEntity<String> updateFlight(@PathVariable Long id, @RequestBody AddFlightDto flightDto) throws FlightNotFoundException, AirlineNotFoundException, AirportNotFoundException {
        String response = flightService.updateFlight(id,flightDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/availableFlight")
    public ResponseEntity<List<FlightSearchDto>> getAvailableFlight (
            @RequestParam (required = false, name = "departurePort") Airport departurePort,
            @RequestParam (required = false, name = "arrivalPort") Airport arrivalPort,
            @RequestParam (required = false, name = "departureDate") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam (required = false, name = "returnDate")  @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(required = false, name = "noOfAdult") int noOfAdult,
            @RequestParam(required = false, name = "noOfChildren") int noOfChildren,
            @RequestParam (required = false, name = "noOfInfant") int noOfInfant

    )
            throws FlightNotFoundException {
        List<FlightSearchDto> availableFlight = flightService.searchAvailableFlight(departurePort,arrivalPort,departureDate,returnDate,noOfAdult, noOfChildren,noOfInfant);
        return ResponseEntity.ok(availableFlight);
    }
    @GetMapping("/fetch-all-flights")
    public ResponseEntity<Page<Flight>> getAllFlights(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {

        return new ResponseEntity<>(flightService.getAllFlights(pageNo, pageSize), HttpStatus.OK);
    }

    @DeleteMapping("/delete-flight/{Id}")
    public ResponseEntity<String> deleteFlight(@PathVariable Long Id) throws FlightNotFoundException {
        String response = flightService.deleteFlight(Id);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
