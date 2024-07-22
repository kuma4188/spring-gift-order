package gift.controller.api;

import gift.dto.OptionDTO;
import gift.service.OptionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class OptionController {

    private final OptionService optionService;

    @Autowired
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public List<OptionDTO> getOptionsByProductId(@PathVariable Long productId) {
        return optionService.getOptionsByProductId(productId);
    }

    @GetMapping("/{id}")
    public OptionDTO getOptionById(@PathVariable Long id) {
        return optionService.getOptionById(id);
    }

    @PostMapping
    public void addOption(@RequestBody OptionDTO optionDTO) {
        optionService.saveOption(optionDTO);
    }

    @PutMapping("/{id}")
    public void updateOption(@PathVariable Long id, @RequestBody OptionDTO optionDTO) {
        optionService.updateOption(id, optionDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteOption(@PathVariable Long id) {
        optionService.deleteOption(id);
    }
}
