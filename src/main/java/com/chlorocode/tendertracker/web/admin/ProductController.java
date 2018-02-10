package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.ProductCreateDTO;
import com.chlorocode.tendertracker.dao.dto.ProductUpdateDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CompanyService;
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
import java.util.Date;

/**
 * Controller for marketplace product page in admin portal.
 */
@Controller
public class ProductController {

    private ProductService productService;
    private CodeValueService codeValueService;
    private CompanyService companyService;

    /**
     * Constructor.
     *
     * @param productService ProductService
     * @param codeValueService CodeValueService
     * @param companyService CompanyService
     */
    @Autowired
    public ProductController(ProductService productService, CodeValueService codeValueService,
                             CompanyService companyService) {
        this.productService = productService;
        this.codeValueService = codeValueService;
        this.companyService = companyService;
    }

    /**
     * This method is used to display product list page.
     *
     * @return String
     */
    @GetMapping("/admin/product")
    public String showProductPage() {
        return "admin/product/productView";
    }

    /**
     * This method is used to display create product page.
     *
     * @param model ModelMap
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @GetMapping("/admin/product/create")
    public String showCreateProductPage(ModelMap model, RedirectAttributes redirectAttrs) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        Company curentCompany = companyService.findById(currentUser.getSelectedCompany().getId());

        // Check if current company is blacklisted
        if (!curentCompany.isActive()) {
            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.DANGER, "Your company is blacklisted. You are not allowed to add new product.");
            redirectAttrs.addFlashAttribute("alert", alertDTO);
            return "redirect:/admin/product";
        }

        model.addAttribute("product", new ProductCreateDTO());
        model.addAttribute("productCategory", codeValueService.getByType("product_category"));
        return "admin/product/productCreate";
    }

    /**
     * This method is used to save new marketplace product.
     *
     * @param form input data by user
     * @param redirectAttributes RedirectAttributes
     * @param modelMap ModelMap
     * @return String
     * @see ProductCreateDTO
     */
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
        product.setCreatedBy(currentUser.getId());
        product.setCreatedDate(new Date());
        product.setLastUpdatedBy(currentUser.getId());
        product.setLastUpdatedDate(new Date());

        try {
            productService.createProduct(product);

            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Product Created");
            redirectAttributes.addFlashAttribute("alert", alertDTO);
        } catch (ApplicationException exception) {
            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.DANGER, exception.getMessage());
            modelMap.addAttribute("alert", alertDTO);
            modelMap.addAttribute("product", form);
            return "admin/product/create";
        }

        return "redirect:/admin/product";
    }

    /**
     * This method is used to display edit product page.
     *
     * @param id unique identifier of the product
     * @param model ModelMap
     * @return String
     */
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

    /**
     * This method is used to update product.
     *
     * @param form input data from user
     * @param redirectAttrs RedirectAttributes
     * @return String
     * @see ProductUpdateDTO
     */
    @PostMapping("/admin/product/update")
    public String updateProduct(@Valid @ModelAttribute("product") ProductUpdateDTO form, RedirectAttributes redirectAttrs) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        Product product = productService.findById(form.getProductCode());

        product.setType(form.getType());
        product.setCategory(form.getCategory());
        product.setTitle(form.getTitle());
        product.setDescription(form.getDescription());
        product.setPublish(form.isPublished());
        product.setPrice(form.getPrice());
        product.setLastUpdatedBy(currentUser.getId());
        product.setLastUpdatedDate(new Date());

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
}
