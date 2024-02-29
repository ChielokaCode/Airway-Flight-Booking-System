package org.chielokacode.airwaycc.airwaybackendcc.repository;

import org.chielokacode.airwaycc.airwaybackendcc.model.Classes;
import org.chielokacode.airwaycc.airwaybackendcc.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassesRepository extends JpaRepository<Classes, Long> {
    List<Classes> findByFlight(Flight flight);

    Optional<Classes> findByFlightId(Long id);

    Classes findByFlightIdAndId(Long id, Long id1);
}
