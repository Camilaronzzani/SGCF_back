package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.BooleanRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;



@Slf4j
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

    public List<Reservation> findAll() {
        try {
            log.info("Reservations list found successfully");
            return reservationRepository.findAll();

        } catch (RuntimeException e) {
            log.error("Error in ReservationService.findAll", e);
            throw new RuntimeException(e);
        }
    }

    public Reservation findById(long id) {
        try {
            Reservation reservation = reservationRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation not found"));

            log.info("Reservations {} found successful ", id);
            return reservation;

        } catch (Exception e) {
            log.error("Error in ReservationService.findById" , e);
            throw new RuntimeException(e);
        }
    }

    public BooleanRequest save(ReservationRequest reservationRequest) {
        try {
            Reservation reservation = toReservation(reservationRequest);
            reservationRepository.save(reservation);
            if (reservation.getDate().isBefore(LocalDate.now())){
                log.warn("the reservations date  can't be before today");
                return new BooleanRequest(false,"the date is before at today ");
            }

            log.info("Reservations for customer {} saved successfully", reservation.getCustomer().getName());
            return new BooleanRequest(true,"Reservation for customer: " + reservation.getCustomer().getName() + " saved successfully ");

        } catch (Exception e) {
            log.error("Error in ReservationService.save" , e);
            throw new RuntimeException(e);
        }
    }


    public Reservation changeDataByReservation(long id, ReservationRequest reservationRequesteservation) {
        Reservation newReservation = toReservation(reservationRequesteservation);
        Reservation reservationOld = reservationRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation not found"));

        reservationOld.setDate(newReservation.getDate());
        reservationOld.setTour(newReservation.getTour());
        reservationOld.setCustomer(newReservation.getCustomer());
        reservationOld.setEmployee(newReservation.getEmployee());
        reservationOld.setValue(newReservation.getValue());
        reservationOld.setStatus(newReservation.getStatus());

        return reservationOld;
    }

    @Transactional
    public Reservation update(ReservationRequest reservationRequest, long id) {
        try {
            Reservation reservation = changeDataByReservation(id , reservationRequest);

            log.info("Reservations {} updated successfully ", id);
            return reservation;
        } catch (Exception e) {
            log.error("Error in ReservationService.update" , e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {
            Reservation reservation = reservationRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation not found"));
            reservation.setActive(false);

            log.info("Reservation {} deactivate successfully", id);

        } catch (Exception e) {
            log.error("Error in ReservationService.update" , e);
            throw new RuntimeException(e);
        }
    }

    public List<Reservation> findAllActive() {
        try {
            log.info("Fetches reservations list active");
            return reservationRepository.findByActiveTrue();
        } catch (Exception e) {
            log.error("Error in ReservationService.findAllActive" , e);
            throw new RuntimeException(e);
        }
    }

    public List<Reservation> findByCustomer(long customerId) {
        try {
            log.info("Fetches reservations for customer");
            return reservationRepository.findByCustomerId(customerId);

        } catch (Exception e) {
            log.error("Error in ReservationService.findByCustomer" , e);
            throw new RuntimeException(e);
        }
    }

    public List<Reservation> findByStatus(Status status) {
        try {
            log.info("Fetches status");
            return reservationRepository.findByStatus(status);

        } catch (Exception e) {
            log.error("Error in ReservationService.findByStatus" , e);
            throw new RuntimeException(e);
        }
    }

    private Tour findTour(Long tourId) {
        if (tourId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tour Id is required");
        }

        log.info("Fetches tour");
        return tourRepository.findById(tourId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tour not found"));
    }

    private Customer findCustomer(Long customerId) {
        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId is required");
        }

        log.info("Fetches customer");
        return customerRepository.findById(customerId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer no find"));
    }

    private Employee findEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }
        log.info("Fetches employee");
        return employeeRepository.findById(employeeId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "employee no find"));
    }
    public  Reservation toReservation(ReservationRequest reservationRequest) {

        Reservation reservation = new Reservation();
        reservation.setDate(reservationRequest.date());
        reservation.setTour(findTour(reservationRequest.tourId()));
        reservation.setCustomer(findCustomer(reservationRequest.customerId()));
        reservation.setEmployee(findEmployee(reservationRequest.employeeId()));
        reservation.setValue(reservationRequest.value());
        reservation.setStatus(reservationRequest.status() == null
                ? Status.Pending
                : reservationRequest.status());
        reservation.setCustomersNotPaying(reservationRequest.customerNotPaying().stream().map(this::findCustomer).toList());
        reservation.setActive(true);
        return reservation;
    }
}
