package org.chielokacode.airwaycc.airwaybackendcc.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(
            name = "airline_airport",
            joinColumns = @JoinColumn(name = "airline_id"),
            inverseJoinColumns = @JoinColumn(name = "airport_iata_code")
    )
    private Set<Airport> airports;
    @OneToMany
    private List<Aircraft> aircraftList;
}
