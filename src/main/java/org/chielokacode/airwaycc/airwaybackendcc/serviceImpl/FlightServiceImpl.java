package org.chielokacode.airwaycc.airwaybackendcc.serviceImpl;


import lombok.extern.slf4j.Slf4j;
import org.chielokacode.airwaycc.airwaybackendcc.dto.AddFlightDto;
import org.chielokacode.airwaycc.airwaybackendcc.dto.FlightSearchDto;
import org.chielokacode.airwaycc.airwaybackendcc.enums.FlightDirection;
import org.chielokacode.airwaycc.airwaybackendcc.enums.Role;
import org.chielokacode.airwaycc.airwaybackendcc.exception.*;
import org.chielokacode.airwaycc.airwaybackendcc.model.*;
import org.chielokacode.airwaycc.airwaybackendcc.repository.*;
import org.chielokacode.airwaycc.airwaybackendcc.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final AirlineRepository airlineRepository;
    private final UserServiceImpl userService;
    private final ClassesRepository classesRepository;
    private final AirportRepository airportRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository, UserRepository userRepository, AirlineRepository airlineRepository, UserServiceImpl userService, ClassesRepository classesRepository, AirportRepository airportRepository, SeatRepository seatRepository) {
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.airlineRepository = airlineRepository;
        this.userService = userService;
        this.classesRepository = classesRepository;
        this.airportRepository = airportRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<FlightSearchDto> searchAvailableFlight(Airport departurePort, Airport arrivalPort, LocalDate departureDate, LocalDate returnDate, int noOfAdult, int noOfChildren, int noOfInfant) throws FlightNotFoundException {
        List<Flight> availableFlight;
        if(returnDate== null){
            availableFlight = flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual(  departurePort,  arrivalPort,  departureDate, noOfAdult, noOfChildren, noOfInfant);
        }else{
            availableFlight = flightRepository.findByDeparturePortAndArrivalPortAndDepartureDateAndReturnDateAndNoOfAdultGreaterThanEqualAndNoOfChildrenGreaterThanEqualAndNoOfInfantGreaterThanEqual( departurePort,   arrivalPort,  departureDate,  returnDate,  noOfAdult,  noOfChildren,  noOfInfant);
        }
        if(availableFlight.isEmpty()){
            throw new FlightNotFoundException("No flight found for specified criteria. Please adjust search parameters");
        }
        List<FlightSearchDto> availableFlightDTOs = new ArrayList<>();
        for (Flight flight : availableFlight) {
            FlightSearchDto flightDTO = new FlightSearchDto();
            flightDTO.setId(flight.getId());
            flightDTO.setFlightDirection(flight.getFlightDirection());
            flightDTO.setFlightNo(flight.getFlightNo());
            flightDTO.setAirline(flight.getAirline().getName());
            flightDTO.setArrivalDate(flight.getArrivalDate());
            flightDTO.setDepartureDate(flight.getDepartureDate());
            flightDTO.setArrivalTime(flight.getArrivalTime());
            flightDTO.setReturnDate(flight.getReturnDate());
            flightDTO.setReturnTime(flight.getReturnTime());
            flightDTO.setDepartureTime(flight.getDepartureTime());
            flightDTO.setDuration(flight.getDuration());
            flightDTO.setArrivalPort(flight.getArrivalPort());
            flightDTO.setDeparturePort(flight.getDeparturePort());
            flightDTO.setClasses(flight.getClasses());
            flightDTO.setTotalSeat(flight.getTotalSeat());
            flightDTO.setAvailableSeat(flight.getAvailableSeat());
            flightDTO.setNoOfChildren(flight.getNoOfChildren());
            flightDTO.setNoOfAdult(flight.getNoOfAdult());
            flightDTO.setNoOfInfant(flight.getNoOfInfant());


            availableFlightDTOs.add(flightDTO);
        }

        return availableFlightDTOs;
    }

    @Override
    public String addNewFlight(AddFlightDto flightDto) throws AirportNotFoundException, AirlineNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findUserByEmail(username);

        if (user == null) {
            throw new UserNotFoundException("Admin must be Logged In to Continue");
        }
        if (!user.getUserRole().equals(Role.ADMIN)) {
            throw new UserNotVerifiedException("You are not allowed to Add New Flight");
        }

        Flight newFlight = new Flight();
        newFlight.setFlightDirection(flightDto.getFlightDirection());

            String newFlightNoLetter = userService.generateRandomLetters(2);
            String newFlightNo = userService.generateRandomNumber(3);
            String generatedFlightNo = newFlightNoLetter + newFlightNo;
            newFlight.setFlightNo(generatedFlightNo);
            newFlight.setUser(user);
            newFlight.setAirlineName(flightDto.getAirlineName());
            newFlight.setAirline(airlineRepository.findByNameIgnoreCase(flightDto.getAirlineName()).orElseThrow(() -> new AirlineNotFoundException("Airline with name not Found")));
            newFlight.setDuration(flightDto.getDuration());
            newFlight.setDepartureDate(flightDto.getDepartureDate());
            LocalDate arrivalDate = calculateArrivalDate(flightDto.getDepartureDate(), flightDto.getDuration());
            newFlight.setArrivalDate(arrivalDate);
            newFlight.setDepartureTime(flightDto.getDepartureTime());
            newFlight.setArrivalPortName(flightDto.getArrivalPortName());
            newFlight.setDeparturePortName(flightDto.getDeparturePortName());

            newFlight.setArrivalPort(airportRepository.findByIataCodeIgnoreCase(flightDto.getArrivalPortName()).orElseThrow(() -> new AirportNotFoundException("Airport with code not Found")));
            newFlight.setDeparturePort(airportRepository.findByIataCodeIgnoreCase(flightDto.getDeparturePortName()).orElseThrow(() -> new AirportNotFoundException("Airport with code not Found")));

            newFlight.setTotalSeat(flightDto.getTotalSeat());
            newFlight.setNoOfAdult(flightDto.getNoOfAdult());
            newFlight.setNoOfChildren(flightDto.getNoOfChildren());
            newFlight.setNoOfInfant(flightDto.getNoOfInfant());
            LocalTime arrivalTime = flightDto.getDepartureTime().plusMinutes(flightDto.getDuration());
            newFlight.setArrivalTime(arrivalTime);

        if (flightDto.getFlightDirection() == FlightDirection.ROUND_TRIP) {
            newFlight.setReturnDate(flightDto.getReturnDate());
            newFlight.setReturnTime(flightDto.getReturnTime());

        }

        Flight saveFlight = flightRepository.save(newFlight);
            List<Classes> classesList = flightDto.getClasses();
            if (classesList != null) {
                for (Classes classes : classesList) {
                    Classes saveClasses = new Classes();
                    saveClasses.setId(classes.getId());
                    saveClasses.setClassName(classes.getClassName());
                    saveClasses.setBasePrice(classes.getBasePrice());
                    saveClasses.setBaggageAllowance(classes.getBaggageAllowance());
                    saveClasses.setFlightStatus(classes.getFlightStatus());
                    saveClasses.setTaxFee(classes.getTaxFee());
                    saveClasses.setSurchargeFee(classes.getSurchargeFee());
                    saveClasses.setServiceCharge(classes.getServiceCharge());
                    saveClasses.setTotalPrice(classes.getBasePrice() + classes.getTaxFee() + classes.getSurchargeFee() + classes.getServiceCharge());
                    saveClasses.setNumOfSeats(classes.getNumOfSeats());
                    saveClasses.setFlight(saveFlight);
                    Classes savedClasses = classesRepository.save(saveClasses);
                    classes.getSeat().setClassName(savedClasses);
                    classes.getSeat().setFlightName(saveFlight);
                    Seat seat = seatRepository.save(classes.getSeat());
                    saveClasses.setSeat(seat);
                }
            }

        return "Flight Added Successfully";
    }

    public LocalDate calculateArrivalDate(LocalDate departureDate, long durationMinutes) {
        long days = durationMinutes / (24 * 60);
        long remainingMinutes = durationMinutes % (24 * 60);
        LocalDate arrivalDate = departureDate.plusDays(days);
        if (remainingMinutes > 0) {
            LocalTime departureTime = LocalTime.of(0, 0);
            LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);
            LocalDateTime arrivalDateTime = departureDateTime.plusMinutes(remainingMinutes);
            arrivalDate = arrivalDateTime.toLocalDate();
        }
        return arrivalDate;
    }


    @Override
    public String updateFlight(@PathVariable Long id, @RequestBody AddFlightDto flightDto) throws FlightNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findUserByEmail(username);

        if (user == null) {
            throw new UserNotFoundException("Admin must be Logged In to Continue");
        }
        if (!user.getUserRole().equals(Role.ADMIN)) {
            throw new UserNotVerifiedException("You are not allowed to Update New Flight");
        }

        Flight flight = flightRepository.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight is not Found"));

        if (!Objects.equals(user.getUsername(), flight.getUser().getUsername())) {
            throw new UserNotVerifiedException("You can't update this flight");
        }

        if (flightDto.getFlightDirection() != null) {
            flight.setFlightDirection(flightDto.getFlightDirection());
            if (flightDto.getFlightDirection() == FlightDirection.ROUND_TRIP) {
                flight.setReturnDate(flightDto.getReturnDate());
                flight.setReturnTime(flightDto.getReturnTime());
            }
        }
        if (flightDto.getFlightNo() != null) {
            String newFlightNoLetter = userService.generateRandomLetters(2);
            String newFlightNo = userService.generateRandomNumber(3);
            String generatedFlightNo = newFlightNoLetter + newFlightNo;
            flight.setFlightNo(generatedFlightNo);
        }
        if (flightDto.getAirlineName() != null) {
            flight.setAirlineName(flightDto.getAirlineName());
        }
        if (flightDto.getDuration() != null) {
            flight.setDuration(flightDto.getDuration());
        }
        if (flightDto.getDepartureDate() != null) {
            flight.setDepartureDate(flightDto.getDepartureDate());
        }
        if (flightDto.getDepartureTime() != null) {
            flight.setDepartureTime(flightDto.getDepartureTime());
        }
        if (flightDto.getArrivalDate() != null && flightDto.getDuration() != null && flightDto.getDepartureDate() != null) {
            LocalDate arrivalDate = calculateArrivalDate(flightDto.getDepartureDate(), flightDto.getDuration());
            flight.setArrivalDate(arrivalDate);
        }
        if (flightDto.getArrivalTime() != null && flightDto.getDepartureTime() != null && flightDto.getDuration() != null) {
            LocalTime arrivalTime = flightDto.getDepartureTime().plusMinutes(flightDto.getDuration());
            flight.setArrivalTime(arrivalTime);
        }
        if (flightDto.getTotalSeat() != null) {
            flight.setTotalSeat(flightDto.getTotalSeat());
        }
        if (flightDto.getNoOfAdult() != null) {
            flight.setNoOfAdult(flightDto.getNoOfAdult());
        }
        if (flightDto.getNoOfChildren() != null) {
            flight.setNoOfChildren(flightDto.getNoOfChildren());
        }
        if (flightDto.getNoOfInfant() != null) {
            flight.setNoOfInfant(flightDto.getNoOfInfant());
        }

        if (flightDto.getArrivalPortName() != null) {
            flight.setArrivalPortName(flightDto.getArrivalPortName());
        }
        if (flightDto.getDeparturePortName() != null) {
            flight.setDeparturePortName(flightDto.getDeparturePortName());
        }

        Flight saveFlight = flightRepository.save(flight);

        List<Classes> classesList = flightDto.getClasses();
        if (classesList != null && !classesList.isEmpty()) {
            List<Classes> existingClassesList = saveFlight.getClasses(); // Fetch all existing classes associated with the flight
            for (Classes classes : classesList) {
                for (Classes existingClasses : existingClassesList) {
//                    if (existingClasses.getId().equals(classes.getId())) { // Match classes by ID
                        // Update class properties
                        if (existingClasses.getClassName() != null) {
                            existingClasses.setClassName(classes.getClassName());
                        }
                        if (existingClasses.getBasePrice() != null) {
                            existingClasses.setBasePrice(classes.getBasePrice());
                        }
                        if (existingClasses.getBaggageAllowance() != null) {
                            existingClasses.setBaggageAllowance(classes.getBaggageAllowance());
                        }
                        if (existingClasses.getFlightStatus() != null) {
                            existingClasses.setFlightStatus(classes.getFlightStatus());
                        }
                        if (existingClasses.getTaxFee() != null) {
                            existingClasses.setTaxFee(classes.getTaxFee());
                        }
                        if (existingClasses.getSurchargeFee() != null) {
                            existingClasses.setSurchargeFee(classes.getSurchargeFee());
                        }
                        if (existingClasses.getServiceCharge() != null) {
                            existingClasses.setServiceCharge(classes.getServiceCharge());
                        }
                        if (existingClasses.getNumOfSeats() != null) {
                            existingClasses.setNumOfSeats(classes.getNumOfSeats());
                        }

                        // Calculate total price
                        Double totalPrice = existingClasses.getBasePrice() + existingClasses.getTaxFee() +
                                existingClasses.getSurchargeFee() + existingClasses.getServiceCharge();
                        existingClasses.setTotalPrice(totalPrice);

                        // Save the updated classes
                        Classes savedClasses = classesRepository.save(existingClasses);

                        // Update seat information
                        Seat seat = savedClasses.getSeat();
                        if (seat != null) {
                            seat.setClassName(savedClasses);
                            seat.setFlightName(saveFlight);
                            seatRepository.save(seat);
                        }
                }
            }
        }

        return "Flight Updated Successfully";
    }


    @Override
    public String deleteFlight(Long Id) throws FlightNotFoundException {
        Flight flight = flightRepository.findById(Id)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found"));
        flightRepository.delete(flight);
        return "Flight deleted successfully";

    }


    @Override
    public Page<Flight> getAllFlights(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return flightRepository.findAll(pageable);
    }
    @Override
    public int getTotalNumberOfFlights() {
        return (int) flightRepository.count();
    }
}
