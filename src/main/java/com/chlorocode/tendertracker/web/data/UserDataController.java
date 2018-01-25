package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.UserDataDAO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * REST controller for user.
 */
@RestController
public class UserDataController {

    private UserDataDAO userDao;

    /**
     * Constructor.
     *
     * @param UserDAO UserDataDAO
     */
    @Autowired
    public UserDataController(UserDataDAO UserDAO) {
        this.userDao = UserDAO;
    }

    /**
     * This method is used to search company user.
     *
     * @param input data table search input
     * @return data table output
     */
    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/companyUser", method = RequestMethod.GET)
    public DataTablesOutput<User> getUserData(@Valid DataTablesInput input) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        return userDao.findAll(input, null, (root, criteriaQuery, cb) -> {
            criteriaQuery.distinct(true);
            return cb.and(
                    cb.equal(root.join("userRoles").join("company").get("id"), companyId),
                    cb.equal(root.get("status"), 1)
            );
        });
    }
}

