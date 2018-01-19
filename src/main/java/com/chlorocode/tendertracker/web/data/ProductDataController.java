package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.ProductDAO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ProductDataController {

    private ProductDAO productDAO;

    @Autowired
    public ProductDataController(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/products", method = RequestMethod.GET)
    public DataTablesOutput<Product> getProducts(@Valid DataTablesInput input) {
        CurrentUser user = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = user.getSelectedCompany().getId();

        DataTablesOutput<Product> products = productDAO.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.join("company").get("id"), companyId);
        });

        return products;
    }
}
