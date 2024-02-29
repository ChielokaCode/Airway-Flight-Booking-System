package org.chielokacode.airwaycc.airwaybackendcc.repository;

import org.chielokacode.airwaycc.airwaybackendcc.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface AirportRepository extends JpaRepository<Airport, String> {

    Optional<Airport> findByIataCodeIgnoreCase(String arrivalPortName);
}

