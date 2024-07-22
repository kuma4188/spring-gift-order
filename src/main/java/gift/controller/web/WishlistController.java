package gift.controller.web;

import gift.dto.Request.AddToWishlistRequest;
import gift.dto.Response.WishlistResponse;
import gift.dto.WishlistDTO;
import gift.service.WishlistService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public String getWishlist(Principal principal, Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "productName") String sort,
        @RequestParam(defaultValue = "asc") String direction) {

        String username = principal.getName();
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<WishlistDTO> wishlistPage = wishlistService.getWishlistByUser1(username, pageable);

        model.addAttribute("wishlistPage", wishlistPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);

        return "wishlist";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<WishlistResponse> addToWishlist(@RequestBody AddToWishlistRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new WishlistResponse(false));
        }
        String username = principal.getName();
        WishlistResponse response = wishlistService.addToWishlist(username, request.getProductId(), request.getQuantity(), request.getOptions());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<WishlistResponse> updateQuantity(@PathVariable("id") Long id, @RequestParam("quantity") int quantity, @RequestParam("optionId") Long optionId) {
        WishlistResponse response = wishlistService.updateQuantity(id, quantity, optionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<WishlistResponse> removeFromWishlist(@PathVariable("id") Long id) {
        WishlistResponse response = wishlistService.removeFromWishlist(id);
        return ResponseEntity.ok(response);
    }
}
