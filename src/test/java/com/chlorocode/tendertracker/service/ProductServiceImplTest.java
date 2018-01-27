package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ProductDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    ProductDAO productDAO;

    @InjectMocks
    private ProductServiceImpl productService;

    @Before
    public void onSetUp() {
        User u = new User("Name", "test@gmail.com", "123456", "password");
        List<Company> managedCompany = new LinkedList<Company>();
        CurrentUser currentUser = new CurrentUser(u, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_SYS_ADMIN"), managedCompany);

        Authentication auth = new UsernamePasswordAuthenticationToken(currentUser,null);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @After
    public void onTearDown() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testBlacklistProduct() {
        Product p = new Product();
        p.setProductCode(1);
        p.setStatus(0);

        when(productDAO.findOne(1)).thenReturn(p);

        productService.blacklistProduct(1);

        ArgumentCaptor<Product> argument = ArgumentCaptor.forClass(Product.class);
        verify(productDAO, times(1)).save(p);
        verify(productDAO).save(argument.capture());
        assertEquals(1, argument.getValue().getStatus());
    }
}
