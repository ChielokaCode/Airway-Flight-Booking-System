package org.chielokacode.airwaycc.airwaybackendcc.service;

import org.chielokacode.airwaycc.airwaybackendcc.dto.AddFlightDto;
import org.chielokacode.airwaycc.airwaybackendcc.dto.FlightSearchDto;
import org.chielokacode.airwaycc.airwaybackendcc.exception.AirlineNotFoundException;
import org.chielokacode.airwaycc.airwaybackendcc.exception.AirportNotFoundException;
import org.chielokacode.airwaycc.airwaybackendcc.exception.FlightNotFoundException;
import org.chielokacode.airwaycc.airwaybackendcc.model.Airport;
import org.chielokacode.airwaycc.airwaybackendcc.model.Flight;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {
    List<FlightSearchDto> searchAvailableFlight(Airport departurePort, Airport arrivalPort, LocalDate departureDate, LocalDate arrivalDate, int noOfAdult, int noOfChildren, int noOfInfant) throws FlightNotFoundException;
    String addNewFlight(AddFlightDto flightDto) throws AirportNotFoundException, AirlineNotFoundException;
    String updateFlight(Long id, AddFlightDto flightDto) throws FlightNotFoundException, AirlineNotFoundException, AirportNotFoundException;
    String deleteFlight(Long Id) throws FlightNotFoundException;
    Page<Flight> getAllFlights(int pageNo, int pageSize);
    int getTotalNumberOfFlights();
}
