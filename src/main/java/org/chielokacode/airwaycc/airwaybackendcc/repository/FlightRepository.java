package org.chielokacode.airwaycc.airwaybackendcc.repository;

import org.chielokacode.airwaycc.airwaybackendcc.model.Airport;
import org.chielokacode.airwaycc.airwaybackendcc.model.Flight;
import org.chielokacode.airwaycc.airwaybackendcc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> searchAllByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultLessThanEqualAndNoOfChildrenLessThanEqualAndNoOfInfantLessThanEqual(Airport departurePort, Airport arrivalPort, LocalDate departureDate, int noOfAdult, int noOfChildren, int noOfInfant);
    List<Flight> searchAllByDeparturePortAndArrivalPortAndArrivalDateAndDepartureDateAndNoOfAdultLessThanEqualAndNoOfChildrenLessThanEqualAndNoOfInfantLessThanEqual( Airport departurePort, Airport arrivalPort, LocalDate departureDate, LocalDate arrivalDate, int noOfAdult, int noOfChildren, int noOfInfant);

    List<Flight> findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(Airport departurePort, Airport arrivalPort, LocalDate departureDate, int noOfAdult, int noOfChildren, int noOfInfant);

    List<Flight> findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(Airport departurePort, Airport arrivalPort, LocalDate departureDate, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant);
}


