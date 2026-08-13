package SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Reservation;

import java.time.LocalDate;

public record ReservationDto(Long id, LocalDate date, TourDto tour, CustomerDto customer,
                             EmployeDto employee, double value, Status status) {
    public static ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getDate(),
                reservation.getTour() == null ? null : TourDto.toDto(reservation.getTour()),
                reservation.getCustomer() == null ? null : CustomerDto.toDto(reservation.getCustomer()),
                reservation.getEmployee() == null ? null : EmployeDto.toDto(reservation.getEmployee()),
                reservation.getValue(),
                reservation.getStatus()
        );
    }
}
