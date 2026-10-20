package SGCF_back.Camilaronzzani.com.github.sgcf_back.ControllerTest;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.TourController;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.TourRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryTour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Tour;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Service.TourService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TourControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TourService tourService;

    @InjectMocks
    private TourController tourController;

    private ObjectMapper objectMapper;

    private Tour sampleTour;
    private TourRequest sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tourController).build();
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
    void findAll_ShouldReturnList() throws Exception {
        when(tourService.findAll()).thenReturn(List.of(sampleTour));

        mockMvc.perform(get("/api/tour/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nameOfTour").value("Iguazu Falls Circuit"));

        verify(tourService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/tour/findId/{id} - Should return tour DTO by ID")
    void findById_ShouldReturnTourDto() throws Exception {
        when(tourService.findById(1L)).thenReturn(sampleTour);

        mockMvc.perform(get("/api/tour/findId/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(tourService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/tour/save - Should create tour and return 201 Created")
    void save_ShouldReturnCreated() throws Exception {
        doNothing().when(tourService).save(any(TourRequest.class));

        mockMvc.perform(post("/api/tour/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated());

        verify(tourService, times(1)).save(any(TourRequest.class));
    }

    @Test
    @DisplayName("PUT /api/tour/update/{id} - Should update tour and return 204 No Content")
    void update_ShouldReturnNoContentWithBody() throws Exception {
        when(tourService.update(any(TourRequest.class), eq(1L))).thenReturn(sampleTour);

        mockMvc.perform(put("/api/tour/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.id").value(1L));

        verify(tourService, times(1)).update(any(TourRequest.class), eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/tour/delete/{id} - Should delete tour and return 204 No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(tourService).delete(1L);

        mockMvc.perform(delete("/api/tour/delete/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(tourService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("GET /api/tour/findAll/active - Should return active tours as DTOs")
    void findAllActive_ShouldReturnList() throws Exception {
        when(tourService.findAllActive()).thenReturn(List.of(sampleTour));

        mockMvc.perform(get("/api/tour/findAll/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(tourService, times(1)).findAllActive();
    }
}