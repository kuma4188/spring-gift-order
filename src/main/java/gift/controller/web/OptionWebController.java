package gift.controller.web;

import gift.dto.OptionDTO;
import gift.dto.ProductDTO;
import gift.service.OptionService;
import gift.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web/products/{productId}/options")
public class OptionWebController {

    private final OptionService optionService;
    private final ProductService productService;

    @Autowired
    public OptionWebController(OptionService optionService, ProductService productService) {
        this.optionService = optionService;
        this.productService = productService;
    }

    @GetMapping
    public String getOptionsByProductId(@PathVariable Long productId, Model model) {
        List<OptionDTO> options = optionService.getOptionsByProductId(productId);
        ProductDTO product = productService.getProductById(productId);
        model.addAttribute("options", options);
        model.addAttribute("product", product);
        return "productOption";
    }
}
