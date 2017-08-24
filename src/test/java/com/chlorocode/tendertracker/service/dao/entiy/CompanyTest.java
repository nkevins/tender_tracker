package com.chlorocode.tendertracker.service.dao.entiy;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Country;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CompanyTest {

    private Company company;

    @Before
    public void setUp() {
        company = new Company();
    }

    @Test
    public void testValidPostalCode() {
        Country country = new Country();
        country.setId("MY");
        company.setCountry(country);

        company.setPostalCode("abc");
        assertTrue(company.isPostalCodeValid());
        company.setPostalCode("12345678");
        assertTrue(company.isPostalCodeValid());

        country.setId("SG");
        company.setPostalCode("123456");
        assertTrue(company.isPostalCodeValid());
    }

    @Test
    public void testInvalidSGPostalCode() {
        Country country = new Country();
        country.setId("SG");
        company.setCountry(country);

        company.setPostalCode("abc");
        assertFalse(company.isPostalCodeValid());
        company.setPostalCode("123");
        assertFalse(company.isPostalCodeValid());
        company.setPostalCode("1234567");
        assertFalse(company.isPostalCodeValid());
        company.setPostalCode("abcdefgh");
        assertFalse(company.isPostalCodeValid());
    }
}
