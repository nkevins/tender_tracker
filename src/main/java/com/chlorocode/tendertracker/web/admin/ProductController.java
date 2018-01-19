package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.ProductCreateDTO;
import com.chlorocode.tendertracker.dao.dto.ProductUpdateDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.ProductClarificationService;
import com.chlorocode.tendertracker.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class ProductController {

    private ProductService productService;
    private CodeValueService codeValueService;
    private ProductClarificationService prodClarSvc;

    @Autowired
    public ProductController(ProductService productService, CodeValueService codeValueService,
                             ProductClarificationService prodClarSvc) {
        this.productService = productService;
        this.codeValueService = codeValueService;
        this.prodClarSvc = prodClarSvc;
    }

    @GetMapping("/admin/product")
    public String showProductPage() {
        return "admin/product/productView";
    }

    @GetMapping("/admin/product/create")
    public String showCreateProductPage(ModelMap model) {
        model.addAttribute("product", new ProductCreateDTO());
        model.addAttribute("productCategory", codeValueService.getByType("product_category"));
        return "admin/product/productCreate";
    }

    @PostMapping("/admin/product/create")
    public String saveCreatedProduct(@Valid @ModelAttribute("product") ProductCreateDTO form, RedirectAttributes redirectAttributes,
                                     ModelMap modelMap) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        Product product = new Product();
        product.setTitle(form.getTitle());
        product.setPrice(form.getPrice());
        product.setDescription(form.getDescription());
        product.setCompany(currentUser.getSelectedCompany());
        product.setType(form.getType());
        product.setCategory(form.getCategory());
        product.setPublish(true);

        try {
            productService.createProduct(product);

            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Product Created");
            redirectAttributes.addFlashAttribute("alert", alertDTO);
        } catch (ApplicationException exception) {
            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.DANGER, exception.getMessage());
            modelMap.addAttribute("alert", alertDTO);
            modelMap.addAttribute("product", form);
            return "admin/tender/tenderCreate";
        }

        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/{id}")
    public String showProductEditPage(@PathVariable(value = "id") Integer id, ModelMap model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/admin/product";
        }

        ProductUpdateDTO productDTO = new ProductUpdateDTO();
        productDTO.setProductCode(product.getProductCode());
        productDTO.setCategory(product.getCategory());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setTitle(product.getTitle());
        productDTO.setType(product.getType());
        productDTO.setPublished(product.isPublish());

        model.addAttribute("product", productDTO);
        model.addAttribute("productCategory", codeValueService.getByType("product_category"));

        return "admin/product/productUpdate";
    }

    @PostMapping("/admin/product/update")
    public String updateTender(@Valid @ModelAttribute("product") ProductUpdateDTO form, RedirectAttributes redirectAttrs) {
        Product product = productService.findById(form.getProductCode());

        product.setType(form.getType());
        product.setCategory(form.getCategory());
        product.setTitle(form.getTitle());
        product.setDescription(form.getDescription());
        product.setPublish(form.isPublished());
        product.setPrice(form.getPrice());

        try {
            productService.updateProduct(product);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/admin/product/" + form.getProductCode();
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Product Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/product";
    }


    @GetMapping("/product/clarification/{id}")
    public String showProductClarification(@PathVariable(value = "productid") Integer id, ModelMap model){
        Product prod = productService.findById(id);
        ProductClarification prodCla = prodClarSvc.findById(id);
        model.addAttribute("product", prod);
        model.addAttribute("productclarification", prodCla);

        return "marketplaceClarification";
    }
}
