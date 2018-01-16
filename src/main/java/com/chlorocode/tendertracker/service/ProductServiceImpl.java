package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ProductDAO;
import com.chlorocode.tendertracker.dao.ProductPagingDAO;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.specs.ProductSpecs;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Product createProduct(Product product, List<MultipartFile> attachments) {
        if (product == null) {
            throw new ApplicationException("Product is null");
        }

        Product result = productDAO.save(product);

        if (attachments != null) {
            for (MultipartFile multipartFile : attachments) {
                // TODO: Write code to upload the files to AWS here
                // TODO: Write code to save the product files to local DB
            }
        }

        if (result != null) {
            // TODO: Write code here to send email notifications
        }

        return result;
    }

    @Override
    public Page<Product> listAllByPage(Pageable pageable) {
        Specification<Product> specification = ProductSpecs.getAll();
        return productPagingDAO.findAll(specification, pageable);
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
}
