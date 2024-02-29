package org.chielokacode.airwaycc.airwaybackendcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chielokacode.airwaycc.airwaybackendcc.enums.FlightDirection;
import org.chielokacode.airwaycc.airwaybackendcc.model.Airport;
import org.chielokacode.airwaycc.airwaybackendcc.model.Classes;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightSearchDto {
    private Long id;
    private FlightDirection flightDirection;
    private String flightNo;
    private String airline;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private long duration;
    private Airport arrivalPort;
    private LocalDate returnDate;
    private LocalTime returnTime;
    private Airport departurePort;
    private List<Classes> classes;
    private int totalSeat;
    private int availableSeat;
    private int noOfChildren;
    private int noOfAdult;
    private int noOfInfant;
}
