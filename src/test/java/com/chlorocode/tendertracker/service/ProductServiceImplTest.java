package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ProductDAO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    ProductDAO productDAO;

    @Mock
    CurrentUser currentUser;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void testBlacklistProduct() {
        Product p = new Product();
        p.setProductCode(1);
        p.setStatus(0);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(1);
        when(productDAO.findOne(1)).thenReturn(p);

        productService.blacklistProduct(1);

        ArgumentCaptor<Product> argument = ArgumentCaptor.forClass(Product.class);
        verify(productDAO, times(1)).save(p);
        verify(productDAO).save(argument.capture());
        assertEquals(1, argument.getValue().getStatus());
    }
}
