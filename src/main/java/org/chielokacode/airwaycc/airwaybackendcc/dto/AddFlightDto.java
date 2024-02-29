package org.chielokacode.airwaycc.airwaybackendcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chielokacode.airwaycc.airwaybackendcc.enums.FlightDirection;
import org.chielokacode.airwaycc.airwaybackendcc.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddFlightDto {
    private Long id;
    private FlightDirection flightDirection;
    private String flightNo;
    private String airlineName;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private LocalDate returnDate;
    private LocalTime returnTime;
    private Long duration;
    private String arrivalPortName;
    private String departurePortName;
    private List<Classes> classes;
    private Set<Seat> seat;
    private Integer totalSeat;
    private Integer availableSeat;
    private Integer noOfChildren;
    private Integer noOfAdult;
    private Integer noOfInfant;
}
