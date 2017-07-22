package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDTO;
import com.chlorocode.tendertracker.dao.dto.UserRoleDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ResourceNotFoundException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;

@Controller
public class AdminController {

    private UserService userService;
    private CodeValueService codeValueService;

    @Autowired
    public AdminController(UserService userService, CodeValueService codeValueService) {
        this.userService = userService;
        this.codeValueService = codeValueService;
    }


    @GetMapping("/admin")
    public String showDashboardPage() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/userDetails")
    public String showUserDetails() {
        return "admin/user/userView";
    }

    @GetMapping("/admin/userDetails/{id}")
    public String displayUserDetail(@PathVariable(value="id") Integer id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        } else {
            model.addAttribute("user", new UserRoleDTO());
            model.addAttribute("reg", user);
            model.addAttribute("userType", codeValueService.getByType("user_type"));
            return "admin/user/assignUerRole";
        }

    }

    @PostMapping("/admin/user/assignRole")
    public String saveCompanyRegistration(@RequestParam("id") int id,
                                          @Valid @ModelAttribute("user") UserRoleDTO form,
                                          BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {

        //UserRole userRole = new UserRole();
        RoleUser role = new RoleUser();
        role.setRoleId(form.getId());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();
        role.setUserId(id);
        role.setCreatedBy(id);
        role.setCreatedDate(new Date());
        role.setLastUpdatedBy(id);
        role.setLastUpdatedDate(new Date());
        role.setCompanyId( usr.getSelectedCompany().getId());
        try{
            RoleUser userRole = userService.addUserRole(role);

            if(userRole != null){
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                        "User Assigned Successfuly");
                redirectAttrs.addFlashAttribute("alert", alert);
                return "redirect:/admin/userDetails";
            }
        }catch(Exception ex){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    ex.getMessage());
            model.addAttribute("alert", alert);
            return "redirect:/";
        }

        return "redirect:/admin/userDetails";
    }
}
