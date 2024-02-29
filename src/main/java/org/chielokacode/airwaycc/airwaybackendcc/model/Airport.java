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
public class Airport {

    @Id
    private String iataCode;
    private String name;
    private String icaoCode;
    private String city;
    private String operationalHrs;
    private String state;

    @ManyToMany(mappedBy = "airports", fetch = FetchType.EAGER)
    private Set<Airline> airlines;
    @ManyToMany
    @JoinTable( // I added
            joinColumns = @JoinColumn(name = "airport_id"),//  I added
            inverseJoinColumns = @JoinColumn(name = "flight_id")// I added
    )
    private List<Flight> flights;


    public Airport(String value, String value1, String value2, String value3, String value4, String value5) {
    }
}
