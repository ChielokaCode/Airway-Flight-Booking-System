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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final SeatListRepository seatListRepository;


    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository, UserRepository userRepository, AirlineRepository airlineRepository, UserServiceImpl userService, ClassesRepository classesRepository, AirportRepository airportRepository, SeatRepository seatRepository, SeatListRepository seatListRepository) {
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.airlineRepository = airlineRepository;
        this.userService = userService;
        this.classesRepository = classesRepository;
        this.airportRepository = airportRepository;
        this.seatRepository = seatRepository;
        this.seatListRepository = seatListRepository;
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
    public String addNewFlight(AddFlightDto flightDto) throws AirportNotFoundException, AirlineNotFoundException, InvalidNumberOfSeatException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Admin must be Logged In to Continue");
        }
        if (!user.getUserRole().equals(Role.ADMIN)) {
            throw new UserNotVerifiedException("You are not allowed to Add New Flight");
        }
        Flight newFlight = new Flight();
        newFlight.setFlightDirection(flightDto.getFlightDirection());

        String newFlightNoLetter = generateRandomLetters(2);
        String newFlightNo = generateRandomNumber(3);
        String generatedFlightNo = newFlightNoLetter + newFlightNo;
        newFlight.setFlightNo(generatedFlightNo);
        newFlight.setUser(user);
        Airline airline = airlineRepository.findByNameIgnoreCase(flightDto.getAirlineName()).orElseThrow(() -> new AirlineNotFoundException("Airline the given name not found"));
        newFlight.setAirline(airline);
        newFlight.setDuration(flightDto.getDuration());
        newFlight.setDepartureDate(flightDto.getDepartureDate());
        newFlight.setDepartureTime(flightDto.getDepartureTime());

        LocalDateTime arrivalDateTime = calculateArrivalDateTime(flightDto.getDepartureDate(), flightDto.getDepartureTime(), flightDto.getDuration());
        newFlight.setArrivalDate(arrivalDateTime.toLocalDate());
        newFlight.setArrivalTime(arrivalDateTime.toLocalTime());

        Airport arrivalPort = airportRepository.findByIataCodeIgnoreCase(flightDto.getArrivalPortName()).orElseThrow(() -> new AirportNotFoundException("Airport with given name not Found"));
        Airport departurePort = airportRepository.findByIataCodeIgnoreCase(flightDto.getDeparturePortName()).orElseThrow(() -> new AirportNotFoundException("Airport with given name not Found"));
        newFlight.setArrivalPort(arrivalPort);
        newFlight.setDeparturePort(departurePort);
        newFlight.setNoOfAdult(flightDto.getNoOfAdult());
        newFlight.setNoOfChildren(flightDto.getNoOfChildren());
        newFlight.setNoOfInfant(flightDto.getNoOfInfant());

        if (flightDto.getFlightDirection() == FlightDirection.ROUND_TRIP) {
            newFlight.setReturnDate(flightDto.getReturnDate());
            newFlight.setReturnTime(flightDto.getReturnTime());
        }
        Flight saveFlight = flightRepository.save(newFlight);

        List<Classes> classesList = flightDto.getClasses();
        if (classesList != null) {
            for (Classes classes : classesList) {
                Classes saveClasses = new Classes();
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
                classes.getSeat().setNumberOfSeat(classes.getSeat().getNumberOfSeat());
                Seat seat = seatRepository.save(classes.getSeat());

                classes.getSeat().setSeatList(generateList(seat));

                saveClasses.setSeat(seat);
                seatRepository.save(seat);
                classesRepository.save(saveClasses);
            }
        }
        return "Flight Added Successfully";
    }

    public List<SeatList> generateList(Seat seat) {
        List<SeatList> resultList = new ArrayList<>();
        for (int i = 1; i <= seat.getNumberOfSeat(); i++) {
            SeatList seatList = new SeatList();
            seatList.setSeatLabel(seat.getSeatCode() + i);
            seatList.setOccupied(false);
            seatList.setSeat(seat);
            resultList.add(seatList);
        }
        return seatListRepository.saveAll(resultList);
    }


    public LocalDateTime calculateArrivalDateTime(LocalDate departureDate, LocalTime departureTime, long durationMinutes) {
        LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);

        long days = durationMinutes / (24 * 60);
        long remainingMinutes = durationMinutes % (24 * 60);

        LocalDateTime arrivalDateTime = departureDateTime.plusDays(days);

        if (remainingMinutes > 0) {
            arrivalDateTime = arrivalDateTime.plusMinutes(remainingMinutes);
        }

        return arrivalDateTime;
    }


    public String generateRandomNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            stringBuilder.append(digit);
        }

        return stringBuilder.toString();
    }

    public String generateRandomLetters(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('A' + random.nextInt(26));
            sb.append(randomChar);
        }
        return sb.toString();
    }

    @Override
    public String updateFlight(@PathVariable Long id, @RequestBody AddFlightDto flightDto) throws FlightNotFoundException, AirportNotFoundException, AirlineNotFoundException {
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
            if (flightDto.getFlightDirection() == FlightDirection.ONE_WAY) {
                flight.setReturnDate(null);
                flight.setReturnTime(null);
            }
        }
        if (flightDto.getFlightNo() != null) {
            String newFlightNoLetter = generateRandomLetters(2);
            String newFlightNo = generateRandomNumber(3);
            String generatedFlightNo = newFlightNoLetter + newFlightNo;
            flight.setFlightNo(generatedFlightNo);
        }
        if (flightDto.getAirlineName() != null) {
            Airline airline = airlineRepository.findByNameIgnoreCase(flightDto.getAirlineName()).orElseThrow(() -> new AirlineNotFoundException("Airline with name not found"));
            flight.setAirline(airline);
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
        if (flightDto.getDepartureDate() != null && flightDto.getDepartureTime() != null && flightDto.getDuration() != null) {
            LocalDateTime arrivalDateTime = calculateArrivalDateTime(flightDto.getDepartureDate(), flightDto.getDepartureTime(), flightDto.getDuration());

            flight.setArrivalDate(arrivalDateTime.toLocalDate());
            flight.setArrivalTime(arrivalDateTime.toLocalTime());
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
            Airport arrivalPort = airportRepository.findByIataCodeIgnoreCase(flightDto.getArrivalPortName()).orElseThrow(() -> new AirportNotFoundException("Airport with code not Found"));
            flight.setArrivalPort(arrivalPort);
        }
        if (flightDto.getDeparturePortName() != null) {
            Airport departurePort = airportRepository.findByIataCodeIgnoreCase(flightDto.getDeparturePortName()).orElseThrow(() -> new AirportNotFoundException("Airport with code not Found"));
            flight.setDeparturePort(departurePort);
        }

        Flight saveFlight = flightRepository.save(flight);

        List<Classes> existingClassesList = flight.getClasses();
        List<Classes> updatedClassesList = flightDto.getClasses();
        if (!(updatedClassesList.isEmpty()) && !(existingClassesList.isEmpty())) {
            for (int i = 0; i < existingClassesList.size(); i++) {
                existingClassesList.get(i).setClassName(updatedClassesList.get(i).getClassName());
                existingClassesList.get(i).setBasePrice(updatedClassesList.get(i).getBasePrice());
                existingClassesList.get(i).setBaggageAllowance(updatedClassesList.get(i).getBaggageAllowance());
                existingClassesList.get(i).setFlightStatus(updatedClassesList.get(i).getFlightStatus());
                existingClassesList.get(i).setTaxFee(updatedClassesList.get(i).getTaxFee());
                existingClassesList.get(i).setSurchargeFee(updatedClassesList.get(i).getSurchargeFee());
                existingClassesList.get(i).setServiceCharge(updatedClassesList.get(i).getServiceCharge());
                existingClassesList.get(i).setTotalPrice(updatedClassesList.get(i).getBasePrice() + updatedClassesList.get(i).getTaxFee() + updatedClassesList.get(i).getSurchargeFee() + updatedClassesList.get(i).getServiceCharge());
                existingClassesList.get(i).setNumOfSeats(updatedClassesList.get(i).getNumOfSeats());
                existingClassesList.get(i).setFlight(saveFlight);
                Classes savedClasses = classesRepository.save(existingClassesList.get(i));
                existingClassesList.get(i).getSeat().setClassName(savedClasses);
                existingClassesList.get(i).getSeat().setFlightName(saveFlight);
                existingClassesList.get(i).getSeat().setSeatCode(updatedClassesList.get(i).getSeat().getSeatCode());
                existingClassesList.get(i).getSeat().setNumberOfSeat(updatedClassesList.get(i).getSeat().getNumberOfSeat());
                Seat seat = seatRepository.save(existingClassesList.get(i).getSeat());
                existingClassesList.get(i).setSeat(seat);
                classesRepository.save(savedClasses);

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
