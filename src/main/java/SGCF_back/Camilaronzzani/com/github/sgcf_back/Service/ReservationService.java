package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.ReservationDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.ReservationRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Employee;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Reservation;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.CustomerRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.EmployeeRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.ReservationRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<ReservationDto> findAll() {
        try {
            List<Reservation> reservationList = reservationRepository.findAll();
            List<ReservationDto> reservationDtos = new ArrayList<>();
            reservationList.forEach(reservation -> {
                ReservationDto reservationDto = ReservationDto.toDto(reservation);
                reservationDtos.add(reservationDto);
            });
            return reservationDtos;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ReservationDto findById(long id) {
        try {
            Optional<Reservation> reservation = Optional.of(reservationRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation no find")));
            return ReservationDto.toDto(reservation.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(ReservationRequest reservationRequest) {
        try {
            Reservation reservation = toReservation(reservationRequest);
            reservationRepository.save(reservation);
            return "Reservation of customer: " + reservation.getCustomer().getName() + " save successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Reservation toReservation(ReservationRequest reservationRequest) {
        Reservation reservation = new Reservation();
        reservation.setDate(reservationRequest.getDate());
        reservation.setTour(findTour(reservationRequest.getTourId()));
        reservation.setCustomer(findCustomer(reservationRequest.getCustomerId()));
        reservation.setEmployee(findEmployee(reservationRequest.getEmployeeId()));
        reservation.setValue(reservationRequest.getValue());
        reservation.setStatus(reservationRequest.getStatus() == null
                ? Status.Pending
                : reservationRequest.getStatus());
        reservation.setActive(true);
        return reservation;
    }

    public void changeDataByReservation(Reservation reservationOld, Reservation newReservation) {
        reservationOld.setDate(newReservation.getDate());
        reservationOld.setTour(newReservation.getTour());
        reservationOld.setCustomer(newReservation.getCustomer());
        reservationOld.setEmployee(newReservation.getEmployee());
        reservationOld.setValue(newReservation.getValue());
        reservationOld.setStatus(newReservation.getStatus());
    }

    public String update(ReservationRequest reservationRequest, long id) {
        try {
            Reservation reservation = toReservation(reservationRequest);
            Reservation reservationOld = Optional.of(reservationRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation no find"))).get();
            changeDataByReservation(reservationOld, reservation);
            reservationRepository.save(reservationOld);
            return "Reservation: " + reservationOld.getId() + " update successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String delete(long id) {
        try {
            Reservation reservation = reservationRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation no find"));
            reservation.setActive(false);
            reservationRepository.save(reservation);
            return "Reservation: " + reservation.getId() + " delete successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String applyPartialUpdate(long id, Map<String, Object> reservation) {
        try {
            Reservation reservation1 = reservationRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation no find"));
            reservation.forEach((key, value) -> {
                switch (key) {
                    case "date" -> reservation1.setDate(LocalDate.parse(value.toString()));
                    case "value" -> reservation1.setValue(Double.parseDouble(value.toString()));
                    case "status" -> reservation1.setStatus(Status.valueOf(value.toString()));
                    case "tourId" -> reservation1.setTour(findTour(Long.parseLong(value.toString())));
                    case "customerId" -> reservation1.setCustomer(findCustomer(Long.parseLong(value.toString())));
                    case "employeeId" -> reservation1.setEmployee(findEmployee(Long.parseLong(value.toString())));
                }
            });
            reservationRepository.save(reservation1);
            return "Reservation: " + reservation1.getId() + " update successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReservationDto> findAllActive() {
        try {
            List<Reservation> reservationList = reservationRepository.findByActiveTrue();
            List<ReservationDto> reservationDtoList = new ArrayList<>();
            reservationList.forEach(reservation -> {
                ReservationDto reservationDto = ReservationDto.toDto(reservation);
                reservationDtoList.add(reservationDto);
            });
            return reservationDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReservationDto> findByCustomer(long customerId) {
        try {
            return reservationRepository.findByCustomerId(customerId)
                    .stream()
                    .map(ReservationDto::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReservationDto> findByStatus(Status status) {
        try {
            return reservationRepository.findByStatus(status)
                    .stream()
                    .map(ReservationDto::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Tour findTour(Long tourId) {
        if (tourId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tourId is required");
        }
        return tourRepository.findById(tourId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour no find"));
    }

    private Customer findCustomer(Long customerId) {
        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId is required");
        }
        return customerRepository.findById(customerId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer no find"));
    }

    private Employee findEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }
        return employeeRepository.findById(employeeId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
    }
}
