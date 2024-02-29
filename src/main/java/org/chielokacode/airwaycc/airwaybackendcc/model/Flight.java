package org.chielokacode.airwaycc.airwaybackendcc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chielokacode.airwaycc.airwaybackendcc.enums.FlightDirection;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private FlightDirection flightDirection;
    @Column(unique = true)
    private String flightNo;
    private String airlineName;
    @ManyToOne
    private Airline airline;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private long duration;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private String arrivalPortName;
    @ManyToOne
    private User user;
    @ManyToOne
    private Airport arrivalPort;
    private String departurePortName;

    @ManyToOne
    private Airport departurePort;

    private LocalDate returnDate;
    private LocalTime returnTime;
    @OneToMany(mappedBy = "flight",cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Classes> classes;
    private int totalSeat;
    private int availableSeat;
    private int noOfChildren;
    private int noOfAdult;
    private int noOfInfant;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "flight_id"), // added
            inverseJoinColumns = @JoinColumn(name = "passenger_id") // added
    )
    private List<Passenger> passengers;
}