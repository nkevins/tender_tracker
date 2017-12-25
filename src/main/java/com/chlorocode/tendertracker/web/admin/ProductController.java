package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.ProductCreateDTO;
import com.chlorocode.tendertracker.service.CodeValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

    private CodeValueService codeValueService;

    public ProductController(CodeValueService codeValueService) {
        this.codeValueService = codeValueService;
    }

    @Autowired


    @GetMapping("/admin/product")
    public String showProductPage() {
        return "admin/product/productView";
    }

    @GetMapping("/admin/product/create")
    public String showCreateProductPage(ModelMap model) {
        model.addAttribute("product", new ProductCreateDTO());
        return "admin/product/productCreate";
    }

}
