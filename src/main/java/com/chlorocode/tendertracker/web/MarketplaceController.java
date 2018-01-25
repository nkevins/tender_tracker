package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.dto.*;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.ProductClarificationService;
import com.chlorocode.tendertracker.service.ProductService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller for marketplace.
 */
@Controller
public class MarketplaceController {

    private ProductService productService;
    private ProductClarificationService prdSvc;
    private CodeValueService codeValueService;
    private UserService userSvc;
    private String className;

    /**
     * Constructor.
     *
     * @param productService ProductService
     * @param codeValueService CodeValueService
     * @param prdSvc ProductClarificationService
     * @param userSvc UserService
     */
    @Autowired
    public MarketplaceController(ProductService productService, CodeValueService codeValueService,ProductClarificationService prdSvc,UserService userSvc) {
        this.productService = productService;
        this.codeValueService = codeValueService;
        this.prdSvc = prdSvc;
        this.userSvc = userSvc;
        className = this.getClass().getName();
    }

    /**
     * This method is used to show product list screen of marketplace.
     *
     * @param pageSize
     * @param page
     * @param modelMap
     * @return String
     */
    @GetMapping("/marketplace")
    public String marketplace(@RequestParam("pageSize") Optional<Integer> pageSize,
                              @RequestParam("page") Optional<Integer> page,
                              ModelMap modelMap) {

        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<Product> products = productService.listAllByPage(
                new PageRequest(
                        evalPage, evalPageSize, new Sort(new Sort.Order(Sort.Direction.ASC, TTConstants.TITLE))
                ));

        Pager pager = new Pager(products.getTotalPages(), products.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        ProductSearchDTO productSearchDTO = new ProductSearchDTO();
        productSearchDTO.setOrderBy(TTConstants.TITLE);
        productSearchDTO.setOrderMode(TTConstants.ASC);
        modelMap.addAttribute("searchCriteria", productSearchDTO);
        modelMap.addAttribute("products", products);
        modelMap.addAttribute("codeValueSvc", codeValueService);
        modelMap.addAttribute("selectedPageSizse", evalPageSize);
        modelMap.addAttribute("pager", pager);

        if (products == null || products.getTotalPages() == 0) {
            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.WARNING, "No Products found.");
            modelMap.addAttribute("alert", alertDTO);
        }

        return "marketplace";
    }

    /**
     * This method is used to show products list of marketplace by advanced search or searchText.
     *
     * @param pageSize
     * @param page
     * @param searchText
     * @param title
     * @param companyName
     * @param orderBy
     * @param orderMode
     * @param modelMap ModelMap
     * @return String
     */
    @GetMapping("/marketplace/products")
    public String showProducts(@RequestParam("pageSize") Optional<Integer> pageSize,
                               @RequestParam("page") Optional<Integer> page,
                               @RequestParam("searchText") Optional<String> searchText,
                               @RequestParam("title") Optional<String> title,
                               @RequestParam("companyName") Optional<String> companyName,
                               @RequestParam("orderBy") Optional<String> orderBy,
                               @RequestParam("orderMode") Optional<String> orderMode,
                               ModelMap modelMap) {
        ProductSearchDTO productSearchDTO = new ProductSearchDTO();
        productSearchDTO.setSearchText(searchText.orElse(null));
        productSearchDTO.setTitle(title.orElse(null));
        productSearchDTO.setCompanyName(companyName.orElse(null));
        productSearchDTO.setOrderBy(orderBy.orElse(null) == null ? TTConstants.TITLE : orderBy.get());
        productSearchDTO.setOrderMode(orderMode.orElse(null) == null ? TTConstants.ASC : orderMode.get());
        productSearchDTO.setAdvance(productSearchDTO.getSearchText() != null
                || productSearchDTO.getTitle() != null
                || productSearchDTO.getCompanyName() != null);

        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<Product> products = productService.searchProduct(productSearchDTO,
//                new PageRequest(evalPage, evalPageSize)
                new PageRequest(
                        evalPage, evalPageSize, getSortPattern(productSearchDTO)
                )
        );

        Pager pager = new Pager(products.getTotalPages(), products.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        modelMap.addAttribute("searchCriteria", productSearchDTO);
        modelMap.addAttribute("products", products);
        modelMap.addAttribute("codeValueSvc", codeValueService);
        modelMap.addAttribute("selectedPageSizse", evalPageSize);
        modelMap.addAttribute("pager", pager);
        if (products == null || products.getTotalPages() == 0) {
            modelMap.addAttribute("noProductFound", true);
        } else {
            modelMap.addAttribute("noProductFound", false);
        }

        return "marketplace";
    }

    /**
     * This method is used the get the sort pattern of the product list screen.
     *
     * @param searchDTO ProductSearchDTO
     * @return Sort
     * @see ProductSearchDTO
     */
    private Sort getSortPattern(ProductSearchDTO searchDTO) {
        Sort.Direction direction = Sort.Direction.ASC;
        // Set order direction.
        if (searchDTO.getOrderMode().equals(TTConstants.DESC)) {
            direction = Sort.Direction.DESC;
        }

        // Set order by attribute.
        return new Sort(new Sort.Order(direction, searchDTO.getOrderBy()));
    }

    /**
     * This method is used to show the detail screen of selected product.
     *
     * @param id unique idendtifier of the product
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/product/clarification/{id}")
    public String showProductDetailsPage(@PathVariable(value = "id") Integer id, ModelMap model){

        Product prod = productService.findById(id);
        List<ProductClarification> prodClar = prdSvc.findClarificationByProdId(id);
        ProductUpdateDTO prodDto = new ProductUpdateDTO();
        model.addAttribute("product", prod);
        model.addAttribute("productclarification", prod.getClarifications());

        model.addAttribute("prodDto",prodDto);
        model.addAttribute("clarification",prodClar);
        return "marketplaceClarification";
    }

    /**
     * This method is used to update the product clarification.
     *
     * @param response description of product clarification
     * @param productId unique identifier of the product
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/product/clarification/save")
    public String updateProduct (@RequestParam("response") String response,  @RequestParam("productId") int productId, RedirectAttributes redirectAttrs) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        ProductClarification clar = new ProductClarification();
        Company comp = new Company();
        comp.setId(currentUser.getSelectedCompany().getId());
        Product p = new Product();
        p.setProductCode(productId);
        clar.setCompany(comp);
        clar.setProduct(p);
        clar.setCreatedBy(currentUser.getId());
        clar.setCreatedDate(new Date());
        clar.setLastUpdatedBy(currentUser.getId());
        clar.setLastUpdatedDate(new Date());
        clar.setDescription(response);

        try {
            prdSvc.create(clar);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            redirectAttrs.addFlashAttribute("alert", alert);
            return "redirect:/product/clarification/" + productId;
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Product Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/product/clarification/" + productId;
    }

    /**
     * This method is used to show the product clarification page.
     *
     * @return String
     */
    @GetMapping("/admin/product/clarification")
    public String showProductClarificationPage() {
        return "admin/product/productClarificationView";
    }

    /**
     * This method is used to show the tender clarification update page.
     *
     * @param id unique identifier of the product clarification.
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/product/clarification/view/{id}")
    public String showTenderClarificationUpdatePage(@PathVariable(value = "id") Integer id,ModelMap model) {
        ProductClarification clarfi = prdSvc.findById(id);
        if(clarfi == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "unable to find the clarification based on this id " + id);
            model.addAttribute("alert", alert);
            return "admin/clarification/tenderClarificationList";
        }

        //perform validation check, if the tender is not created by this company, stop to view it
        CurrentUser usr1 = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(clarfi.getProduct().getCompany().getId() != usr1.getSelectedCompany().getId())
        {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "You are not authorized to view the tender clarification details");
            model.addAttribute("alert", alert);
            return "admin/clarification/tenderClarificationList";
        }
        try{
            User usr = userSvc.findById(clarfi.getCreatedBy());
            TenderClarificationDTO dto = new TenderClarificationDTO();
            dto.setId(clarfi.getId());
            dto.setClarification(clarfi.getDescription());
            dto.setTitle(clarfi.getProduct().getTitle());
            dto.setDescription(clarfi.getProduct().getDescription());
            dto.setSubmittedByCompany(clarfi.getCompany().getName());
            dto.setSubmittedByName(usr.getName());
            dto.setSubmittedByContactNo(usr.getContactNo());
            dto.setSubmittedByEmail(usr.getEmail());
            dto.setSubmittedDate(clarfi.getCreatedDate());
            dto.setResponse(clarfi.getResponse());
            dto.setPrice(clarfi.getProduct().getPrice());
            List<CodeValue> lstCode = codeValueService.getByType("product_category");
            for(int i = 0; i < lstCode.size(); i++){
                CodeValue code = lstCode.get(i);
                if(code.getCode() == clarfi.getProduct().getCategory()){
                    dto.setCategory(code.getDescription());
                    break;
                }
            }

            //dto.setTenderType(clarfi.getTender().getTenderType());
            model.addAttribute("clarificationDto",dto);
        }catch(Exception ex){
            TTLogger.error(className,"error: ", ex );
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Failed to retrieve tender clarification.Please contact administrator");
            model.addAttribute("alert", alert);
            return "admin/clarification/tenderClarificationList";
        }


        return "admin/product/productClarificationFormView";
    }

    /**
     * This method is used to update the tender clarification response.
     *
     * @param form TenderClarificationDTO
     * @param model ModelMap
     * @return String
     */
    @PostMapping("/admin/product/clarification/update")
    public String updateTenderClarificationResponse(@Valid TenderClarificationDTO form, ModelMap model){

        ProductClarification clari = prdSvc.updateResponse(form.getId(),form.getResponse());
        if(clari == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Failed to update tender clarification response.Please contact administrator");
            model.addAttribute("alert", alert);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                    "Update tender clarification response successfully");
            model.addAttribute("alert", alert);
        }

        return "admin/product/productClarificationView";
    }
}
