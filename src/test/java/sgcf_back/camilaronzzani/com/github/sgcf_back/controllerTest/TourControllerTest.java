package sgcf_back.camilaronzzani.com.github.sgcf_back.controllerTest;

import sgcf_back.camilaronzzani.com.github.sgcf_back.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.TourController;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.TourRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Tour;
import sgcf_back.camilaronzzani.com.github.sgcf_back.service.TourService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourController.class)
@Import(SecurityConfig.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TourService tourService;

    private ObjectMapper objectMapper;

    private Tour sampleTour;
    private TourRequest sampleRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        sampleTour = new Tour();
        sampleTour.setId(1L);
        sampleTour.setPrice(250.0);
        sampleTour.setKmOftour(120L);
        sampleTour.setNameOfTour("Iguazu Falls Circuit");
        sampleTour.setLocations("Foz do Iguacu");
        sampleTour.setActive(true);

        sampleRequest = new TourRequest(
                250.0,
                null,
                120L,
                "Iguazu Falls Circuit",
                "Foz do Iguacu"
        );
    }

    @Test
    @DisplayName("GET /api/tour/findAll - Should return list of tours")
    void findAllReturnsListOfTours() throws Exception {
        when(tourService.findAll()).thenReturn(List.of(sampleTour));

        ResultActions resposta = mockMvc.perform(get("/api/tour/findAll"));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nameOfTour").value("Iguazu Falls Circuit"));
        verify(tourService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/tour/findId/{id} - Should return tour DTO by ID")
    void findByIdReturnsTourWhenItExists() throws Exception {
        when(tourService.findById(1L)).thenReturn(sampleTour);

        ResultActions resposta = mockMvc.perform(get("/api/tour/findId/{id}", 1L));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(tourService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/tour/save - Should create tour and return 201 Created")
    void saveReturns201WhenRequestIsValid() throws Exception {
        doNothing().when(tourService).save(any(TourRequest.class));

        ResultActions resposta = mockMvc.perform(post("/api/tour/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        resposta.andExpect(status().isCreated());
        verify(tourService, times(1)).save(any(TourRequest.class));
    }

    @Test
    @DisplayName("PUT /api/tour/update/{id} - Should update tour and return 204 No Content")
    void updateReturns204WithUpdatedTour() throws Exception {
        when(tourService.update(any(TourRequest.class), eq(1L))).thenReturn(sampleTour);

        ResultActions resposta = mockMvc.perform(put("/api/tour/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));

        resposta.andExpect(status().isNoContent())
                .andExpect(jsonPath("$.id").value(1L));
        verify(tourService, times(1)).update(any(TourRequest.class), eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/tour/delete/{id} - Should delete tour and return 204 No Content")
    void deleteReturns204WhenTourIsDeleted() throws Exception {
        doNothing().when(tourService).delete(1L);

        ResultActions resposta = mockMvc.perform(delete("/api/tour/delete/{id}", 1L));

        resposta.andExpect(status().isNoContent());
        verify(tourService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("GET /api/tour/findAll/active - Should return active tours as DTOs")
    void findAllActiveReturnsListOfActiveTours() throws Exception {
        when(tourService.findAllActive()).thenReturn(List.of(sampleTour));

        ResultActions resposta = mockMvc.perform(get("/api/tour/findAll/active"));

        resposta.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(tourService, times(1)).findAllActive();
    }
}