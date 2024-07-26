package gift.controller.web;

import gift.dto.CategoryDTO;
import gift.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web/categories")
public class CategoryWebController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryWebController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getCategoriesWeb(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category";
    }

    @GetMapping("/{id}")
    public String getCategoryById(@PathVariable("id") int id, Model model) {
        CategoryDTO category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "categoryDetail";
    }

    // 추가: /api/categories/web 경로 처리
    @GetMapping("/web")
    public String getCategoriesForWeb(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category"; // category.html 반환
    }
}
