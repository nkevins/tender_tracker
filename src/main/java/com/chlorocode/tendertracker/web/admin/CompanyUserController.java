package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyUserAddDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.UserRoleService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for company user management page in admin portal.
 */
@Controller
public class CompanyUserController {

    private UserRoleService userRoleService;
    private UserService userService;

    /**
     * Constructor.
     *
     * @param userRoleService UserRoleService
     * @param userService UserService
     */
    @Autowired
    public CompanyUserController(UserRoleService userRoleService, UserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }

    /**
     * This method is used to display page to list company user.
     *
     * @return String
     */
    @GetMapping("/admin/companyUser")
    public String showCompanyUserList() {
        return "admin/user/userList";
    }

    /**
     * This method is used to display add company user page.
     *
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/companyUser/add")
    public String showAddUserPage(ModelMap model) {
        model.addAttribute("form", new CompanyUserAddDTO());

        return "admin/user/userAdd";
    }

    /**
     * This method is used to save new company user.
     *
     * @param form data inputted by user
     * @param model ModelMap
     * @param redirectAttrs RedirectAttributes
     * @return String
     * @see CompanyUserAddDTO
     */
    @PostMapping("/admin/companyUser/add")
    public String addCompanyUser(@Valid @ModelAttribute("form") CompanyUserAddDTO form, ModelMap model,
                                 RedirectAttributes redirectAttrs) {
        Optional<User> user = userService.findByEmail(form.getEmail());
        if (!user.isPresent()) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "User not exist");
            model.addAttribute("alert", alert);
            model.addAttribute("form", form);
            return "admin/user/userAdd";
        }

        if (form.getRoles() == null || form.getRoles().size() == 0) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Please select at least one role");
            model.addAttribute("alert", alert);
            model.addAttribute("form", form);
            return "admin/user/userAdd";
        }

        List<Role> roles = new LinkedList<>();
        for(int r : form.getRoles()) {
            roles.add(userRoleService.findRoleById(r));
        }

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            userRoleService.addUserRole(user.get(), roles, usr.getSelectedCompany(), usr.getId());
        } catch(ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
            return "admin/user/userAdd";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "User Added");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/companyUser";
    }

    /**
     * This method is used to display company user details page.
     *
     * @param id unique identifier of the user
     * @param model ModelMap
     * @return String
     */
    @GetMapping("/admin/companyUser/{id}")
    public String showCompanyUserDetails(@PathVariable(value="id") int id, ModelMap model) {
        User usr = userService.findById(id);
        if (usr == null) {
            return "redirect:/admin/companyUser";
        }

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Find selected user roles for currently managed company
        int companyId = currentUser.getSelectedCompany().getId();
        List<Role> rolesObjList = userRoleService.findCompanyUserRole(usr.getId(), companyId);
        List<Integer> roles = new LinkedList<>();
        for (Role r : rolesObjList) {
            roles.add(r.getId());
        }

        model.addAttribute("user", usr);
        model.addAttribute("roles", roles);

        return "admin/user/updateUserRole";
    }

    /**
     * This method is used to update company user details.
     *
     * @param roles new list roles to replace existing roles
     * @param userId unique identifier of the user
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/companyUser/update")
    public String editCompanyUser(@RequestParam("roles") List<Integer> roles, @RequestParam("userId") int userId,
                                  RedirectAttributes redirectAttrs) {
        User usr = userService.findById(userId);
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Role> newRoles = new LinkedList<>();
        for (int r : roles) {
            newRoles.add(userRoleService.findRoleById(r));
        }

        userRoleService.updateUserRole(usr, newRoles, currentUser.getSelectedCompany(), currentUser.getId());

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "User Updated");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/companyUser";
    }

    /**
     * This method is used to remove company user.
     *
     * @param userId unique identifier of the user
     * @param redirectAttrs RedirectAttributes
     * @return String
     */
    @PostMapping("/admin/companyUser/remove")
    public String removeCompanyUser(@RequestParam("id") int userId, RedirectAttributes redirectAttrs) {
        User usr = userService.findById(userId);
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userRoleService.removeUserFromCompany(usr, currentUser.getSelectedCompany());

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "User Removed");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/admin/companyUser";
    }
}
