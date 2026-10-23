package sgcf_back.camilaronzzani.com.github.sgcf_back.ControllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.ReservationController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.ReservationRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Employee;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Reservation;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Tour;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private ObjectMapper objectMapper;

    private Reservation sampleReservation;
    private ReservationRequest sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Tour sampleTour = new Tour();
        sampleTour.setId(1L);

        Customer sampleCustomer = new Customer();
        sampleCustomer.setId(1L);

        Employee sampleEmployee = new Employee();
        sampleEmployee.setId(1L);

        sampleReservation = new Reservation();
        sampleReservation.setId(1L);
        sampleReservation.setDate(LocalDate.now());
        sampleReservation.setTour(sampleTour);
        sampleReservation.setCustomer(sampleCustomer);
        sampleReservation.setEmployee(sampleEmployee);
        sampleReservation.setValue(150.0);
        sampleReservation.setStatus(Status.Pending);
        sampleReservation.setCustomersNotPaying(new ArrayList<>());
        sampleReservation.setActive(true);

        sampleRequest = new ReservationRequest(
                LocalDate.now().plusDays(1),
                1L,
                1L,
                1L,
                Status.Pending,
                List.of(2L, 3L)
        );
    }

    @Test
    @DisplayName("GET /api/reservation/findAll - Should return list of reservations")
    void findAllReturnsListOfReservations() throws Exception {
        // Given
        when(reservationService.findAll()).thenReturn(List.of(sampleReservation));

        // When
        ResultActions resposta = mockMvc.perform(get("/api/reservation/findAll"));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(reservationService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/reservation/findId/{id} - Should return reservation by ID")
    void findByIdReturnsReservationWhenItExists() throws Exception {
        // Given
        when(reservationService.findById(1L)).thenReturn(sampleReservation);

        // When
        ResultActions resposta = mockMvc.perform(get("/api/reservation/findId/{id}", 1L));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(reservationService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/reservation/save - Should create reservation and return 201 Created")
    void saveReturns201WhenRequestIsValid() throws Exception {
        // Given
        doNothing().when(reservationService).save(any(ReservationRequest.class));

        // When
        ResultActions resposta = mockMvc.perform(post("/api/reservation/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        // Then
        resposta.andExpect(status().isCreated());
        verify(reservationService, times(1)).save(any(ReservationRequest.class));
    }

    @Test
    @DisplayName("PUT /api/reservation/update/{id} - Should update reservation")
    void updateReturnsUpdatedReservation() throws Exception {
        // Given
        when(reservationService.update(any(ReservationRequest.class), eq(1L))).thenReturn(sampleReservation);

        // When
        ResultActions resposta = mockMvc.perform(put("/api/reservation/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(reservationService, times(1)).update(any(ReservationRequest.class), eq(1L));
    }

    @Test
    @DisplayName("PATCH /api/reservation/updateStatus/{id} - Should update status")
    void updateStatusReturnsReservationWithNewStatus() throws Exception {
        // Given
        Map<String, Status> statusMap = new HashMap<>();
        statusMap.put("status", Status.Pending);

        when(reservationService.updateStatus(1L, Status.Pending)).thenReturn(sampleReservation);

        // When
        ResultActions resposta = mockMvc.perform(patch("/api/reservation/updateStatus/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusMap)));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(reservationService, times(1)).updateStatus(1L, Status.Pending);
    }

    @Test
    @DisplayName("DELETE /api/reservation/delete/{id} - Should delete reservation and return 204 No Content")
    void deleteReturns204WhenReservationIsDeleted() throws Exception {
        // Given
        doNothing().when(reservationService).delete(1L);

        // When
        ResultActions resposta = mockMvc.perform(delete("/api/reservation/delete/{id}", 1L));

        // Then
        resposta.andExpect(status().isNoContent());
        verify(reservationService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("GET /api/reservation/findAll/active - Should return active reservations")
    void findAllActiveReturnsListOfActiveReservations() throws Exception {
        // Given
        when(reservationService.findAllActive()).thenReturn(List.of(sampleReservation));

        // When
        ResultActions resposta = mockMvc.perform(get("/api/reservation/findAll/active"));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(reservationService, times(1)).findAllActive();
    }

    @Test
    @DisplayName("GET /api/reservation/findByCustomer/{customerId} - Should return customer reservations")
    void findByCustomerReturnsCustomerReservations() throws Exception {
        // Given
        when(reservationService.findByCustomer(1L)).thenReturn(List.of(sampleReservation));

        // When
        ResultActions resposta = mockMvc.perform(get("/api/reservation/findByCustomer/{customerId}", 1L));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(reservationService, times(1)).findByCustomer(1L);
    }

    @Test
    @DisplayName("GET /api/reservation/findByStatus/{status} - Should return reservations filtered by status")
    void findByStatusReturnsReservationsWithThatStatus() throws Exception {
        // Given
        when(reservationService.findByStatus(Status.Pending)).thenReturn(List.of(sampleReservation));

        // When
        ResultActions resposta = mockMvc.perform(get("/api/reservation/findByStatus/{status}", Status.Pending));

        // Then
        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(reservationService, times(1)).findByStatus(Status.Pending);
    }
}