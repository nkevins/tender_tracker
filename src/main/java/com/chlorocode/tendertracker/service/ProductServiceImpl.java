package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ProductDAO;
import com.chlorocode.tendertracker.dao.ProductPagingDAO;
import com.chlorocode.tendertracker.dao.dto.ProductSearchDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.specs.ProductSpecs;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductDAO productDAO;
    private ProductPagingDAO productPagingDAO;
    private S3Wrapper s3Wrapper;
    private NotificationService notificationService;

    @Autowired
    public ProductServiceImpl(ProductDAO productDAO, ProductPagingDAO productPagingDAO, S3Wrapper s3Wrapper, NotificationService notificationService) {
        this.productDAO = productDAO;
        this.productPagingDAO = productPagingDAO;
        this.s3Wrapper = s3Wrapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        Product result = productDAO.save(product);
        return result;
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
        int companyID = getCompanyID();

        Specification<Product> specification = null;

        if (productSearchDTO.getSearchText() != null
                && !productSearchDTO.getSearchText().trim().isEmpty()) {
            specification = ProductSpecs.byProductSearchString(productSearchDTO.getSearchText().trim());
            productSearchDTO.setCompanyName(null);
            productSearchDTO.setTitle(null);
        } else {
            specification = ProductSpecs.byProductSearchCriteria(productSearchDTO.getTitle() == null ? null : productSearchDTO.getTitle().trim(),
                    productSearchDTO.getCompanyName() == null ? null : productSearchDTO.getCompanyName().trim());
            productSearchDTO.setSearchText(null);
        }

        return productPagingDAO.findAll(specification, pageable);
    }

    @Override
    public Product findById(int id) {
        return productDAO.findOne(id);
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public S3Wrapper getS3Wrapper() {
        return s3Wrapper;
    }

    public void setS3Wrapper(S3Wrapper s3Wrapper) {
        this.s3Wrapper = s3Wrapper;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private int getCompanyID() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null && principal instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) principal;

            if (currentUser != null && currentUser.getSelectedCompany() != null) {
                return currentUser.getSelectedCompany().getId();
            }
        }

        return 0;
    }
}
