package Controller;

import Service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/Tour")
public class TourController {
    @Autowired
    private TourService tourService;
}
