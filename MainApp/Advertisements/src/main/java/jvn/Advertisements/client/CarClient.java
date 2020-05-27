package jvn.Advertisements.client;
import jvn.Advertisements.dto.response.CarWithAllInformationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cars")
public interface CarClient {

    @GetMapping("/api/car/verify/{carId}")
    CarWithAllInformationDTO verify(@PathVariable("carId") Long carId);
}
