package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ProductDAO;
import com.chlorocode.tendertracker.dao.ProductPagingDAO;
import com.chlorocode.tendertracker.dao.dto.ProductSearchDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.specs.ProductSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Service implementation of ProductService.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private ProductDAO productDAO;
    private ProductPagingDAO productPagingDAO;

    /**
     * Constructor.
     *
     * @param productDAO ProductDAO
     * @param productPagingDAO ProductPagingDAO
     */
    @Autowired
    public ProductServiceImpl(ProductDAO productDAO, ProductPagingDAO productPagingDAO) {
        this.productDAO = productDAO;
        this.productPagingDAO = productPagingDAO;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productDAO.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productDAO.save(product);
    }

    @Override
    public Page<Product> listAllByPage(Pageable pageable) {
        Specification<Product> specification = ProductSpecs.getAll();
        return productPagingDAO.findAll(specification, pageable);
    }

    @Override
    public Page<Product> searchProduct(ProductSearchDTO productSearchDTO, Pageable pageable) {
        Specification<Product> specification;

        if (productSearchDTO.getSearchText() != null
                && !productSearchDTO.getSearchText().trim().isEmpty()) {
            specification = ProductSpecs.byProductSearchString(productSearchDTO.getSearchText().trim());
            productSearchDTO.setCompanyName(null);
            productSearchDTO.setTitle(null);
        } else {
            specification = ProductSpecs.byProductSearchCriteria(
                    productSearchDTO.getTitle() == null ? null : productSearchDTO.getTitle().trim()
                    , productSearchDTO.getCompanyName() == null ? null : productSearchDTO.getCompanyName().trim());
            productSearchDTO.setSearchText(null);
        }

        return productPagingDAO.findAll(specification, pageable);
    }

    @Override
    public Product blacklistProduct(int productCode) {
        Product p = productDAO.findOne(productCode);
        p.setStatus(1);
        p.setLastUpdatedDate(new Date());
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        p.setLastUpdatedBy(usr.getId());
        return productDAO.save(p);
    }

    @Override
    public Product findById(int id) {
        return productDAO.findOne(id);
    }
}
