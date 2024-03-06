package org.chielokacode.airwaycc.airwaybackendcc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Classes className;
    private String seatCode;
    private int occupiedSeats;
    private int unoccupiedSeats;
    private int numberOfSeat;

    @OneToMany
    private List<SeatList> seatList;

    @ManyToOne
    private Flight flightName;
}
