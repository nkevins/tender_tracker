package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDTO;
import com.chlorocode.tendertracker.dao.dto.UserRoleDTO;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.dao.entity.UserRole;
import com.chlorocode.tendertracker.exception.ResourceNotFoundException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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
    public String saveCompanyRegistration(@Valid @ModelAttribute("user") UserRoleDTO form,
                                          BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {

        //UserRole userRole = new UserRole();


        return "";
    }
}
