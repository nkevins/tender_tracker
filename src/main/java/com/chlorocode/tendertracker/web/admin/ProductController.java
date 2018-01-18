package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.ProductCreateDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Controller
public class ProductController {

    private ProductService productService;
    private CodeValueService codeValueService;
    private ProductClarificationService prodClarSvc;
    private S3Wrapper s3Wrapper;
    private UserService userService;
    private CompanyService companyService;

    @Autowired
    public ProductController(ProductService productService, CodeValueService codeValueService, S3Wrapper s3Wrapper,
                             UserService userService, CompanyService companyService,ProductClarificationService prodClarSvc) {
        this.productService = productService;
        this.codeValueService = codeValueService;
        this.s3Wrapper = s3Wrapper;
        this.userService = userService;
        this.companyService = companyService;
        this.prodClarSvc = prodClarSvc;
    }

    @GetMapping("/admin/product")
    public String showProductPage() {
        return "admin/product/productView";
    }

    @GetMapping("/admin/product/create")
    public String showCreateProductPage(ModelMap model) {
        model.addAttribute("product", new ProductCreateDTO());
        return "admin/product/productCreate";
    }

    @GetMapping("/product/clarification/{id}")
    public String showProductClarification(@PathVariable(value = "productid") Integer id, ModelMap model){
        Product prod = productService.findById(id);
        ProductClarification prodCla = prodClarSvc.findById(id);
        model.addAttribute("product", prod);
        model.addAttribute("productclarification", prodCla);

        return "marketplaceClarification";
    }


    @PostMapping("/admin/product/create")
    public String saveCreatedProduct(@Valid @ModelAttribute("product") ProductCreateDTO form, BindingResult result,
                                     RedirectAttributes redirectAttributes, ModelMap modelMap, HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
        String requestedWidth = request.getHeader("X-Requested-With");
        Boolean isAjax = requestedWidth != null && "XMLHttpRequest".equals(requestedWidth);

        if (result.hasErrors()) {
            AlertDTO alertDTO = new AlertDTO(result.getAllErrors());
            modelMap.addAttribute("alert", alertDTO);
            modelMap.addAttribute("product", form);

            if (!isAjax) {
                return "admin/product/productCreate";
            }

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(result.getAllErrors().get(0).getDefaultMessage());

            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        Product product = new Product();
        product.setTitle(form.getTitle());
        product.setPrice(form.getPrice());
        product.setDescription(form.getDescription());
        product.setCompany(currentUser.getSelectedCompany());

        try {
            productService.createProduct(product, form.getAttachments());

            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Product Created");
            redirectAttributes.addFlashAttribute("alert", alertDTO);
        } catch (ApplicationException exception) {
            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.DANGER, exception.getMessage());
            modelMap.addAttribute("alert", alertDTO);
            modelMap.addAttribute("product", form);

            if (!isAjax) {
                return "admin/product/productCreate";
            }

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(exception.getMessage());

            return null;
        }

        if (!isAjax) {
            return "redirect:/admin/product";
        }

        response.setStatus(HttpServletResponse.SC_OK);

        return null;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public CodeValueService getCodeValueService() {
        return codeValueService;
    }

    public void setCodeValueService(CodeValueService codeValueService) {
        this.codeValueService = codeValueService;
    }

    public S3Wrapper getS3Wrapper() {
        return s3Wrapper;
    }

    public void setS3Wrapper(S3Wrapper s3Wrapper) {
        this.s3Wrapper = s3Wrapper;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

}
