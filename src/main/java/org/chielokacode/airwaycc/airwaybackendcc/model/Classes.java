package org.chielokacode.airwaycc.airwaybackendcc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chielokacode.airwaycc.airwaybackendcc.enums.FlightStatus;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String className;
    private Double basePrice;
    private String baggageAllowance;
    @Enumerated(EnumType.STRING)
    private FlightStatus flightStatus;
    private Double taxFee;
    private Double surchargeFee;
    private Double serviceCharge;
    private Double totalPrice;
    private Integer numOfSeats;

    @OneToOne
    private Seat seat;
    @ManyToOne
    private Flight flight;
    @ManyToMany
    private List<Passenger> passengers;
}
