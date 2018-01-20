package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.Pager;
import com.chlorocode.tendertracker.dao.dto.ProductSearchDTO;
import com.chlorocode.tendertracker.dao.dto.ProductUpdateDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class MarketplaceController {

    private ProductService productService;
    private CodeValueService codeValueService;

    @Autowired
    public MarketplaceController(ProductService productService, CodeValueService codeValueService) {
        this.productService = productService;
        this.codeValueService = codeValueService;
    }

    @GetMapping("/marketplace")
    public String marketplace(@RequestParam("pageSize") Optional<Integer> pageSize,
                              @RequestParam("page") Optional<Integer> page,
                              ModelMap modelMap) {

        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<Product> products = productService.listAllByPage(
                new PageRequest(
                        evalPage, evalPageSize, new Sort(new Sort.Order(Sort.Direction.DESC, TTConstants.TITLE))
                ));

        Pager pager = new Pager(products.getTotalPages(), products.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        ProductSearchDTO productSearchDTO = new ProductSearchDTO();
        productSearchDTO.setOrderBy(TTConstants.TITLE);
        modelMap.addAttribute("searchCriteria", productSearchDTO);
        modelMap.addAttribute("products", products);
        modelMap.addAttribute("codeValueSvc", codeValueService);
        modelMap.addAttribute("selectedPageSizse", evalPageSize);
        modelMap.addAttribute("pager", pager);

        if (products == null) {
            AlertDTO alertDTO = new AlertDTO(AlertDTO.AlertType.WARNING, "No Products found.");
            modelMap.addAttribute("alert", alertDTO);
        }

        return "marketplace";
    }

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
        productSearchDTO.setOrderMode(orderMode.orElse(null) == null ? TTConstants.DEFAULT_SORT_DIRECTION : orderMode.get());
        productSearchDTO.setAdvance(productSearchDTO.getSearchText() != null
                || productSearchDTO.getTitle() != null
                || productSearchDTO.getCompanyName() != null);

        int evalPageSize = pageSize.orElse(TTConstants.INITIAL_PAGE_SIZE);
        int evalPage = (page.orElse(0) < 1) ? TTConstants.INITIAL_PAGE : page.get() - 1;

        Page<Product> products = productService.searchProduct(productSearchDTO,
                new PageRequest(evalPage, evalPageSize)
        );

        Pager pager = new Pager(products.getTotalPages(), products.getNumber(), TTConstants.BUTTONS_TO_SHOW);

        modelMap.addAttribute("searchCriteria", productSearchDTO);
        modelMap.addAttribute("products", products);
        modelMap.addAttribute("codeValueSvc", codeValueService);
        modelMap.addAttribute("selectedPageSizse", evalPageSize);
        modelMap.addAttribute("pager", pager);

        return "marketplace";
    }

    @GetMapping("/product/clarification/{id}")
    public String showProductDetailsPage(@PathVariable(value = "id") Integer id, ModelMap model){

        Product prod = productService.findById(id);
        ProductUpdateDTO prodDto = new ProductUpdateDTO();
        model.addAttribute("product", prod);
        model.addAttribute("productclarification", prod.getClarifications());

        model.addAttribute("prodDto",prodDto);

        return "marketplaceClarification";
    }
}
