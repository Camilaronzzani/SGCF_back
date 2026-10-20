package sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto;

import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public record ReservationDto(Long id, LocalDate date, TourDto tour, CustomerDto customer,
                             EmployeDto employee, double value, Status status, List<CustomerDto>customerNotPaying) {
    public static ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getDate(),
                reservation.getTour() == null ? null : TourDto.toDto(reservation.getTour()),
                reservation.getCustomer() == null ? null : CustomerDto.toDto(reservation.getCustomer()),
                reservation.getEmployee() == null ? null : EmployeDto.toDto(reservation.getEmployee()),
                reservation.getValue(),
                reservation.getStatus(),
                reservation.getCustomersNotPaying().stream().map(CustomerDto ::toDto).toList()
        );
    }
}
