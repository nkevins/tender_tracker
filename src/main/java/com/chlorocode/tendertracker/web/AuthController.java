package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.LoginDTO;
import com.chlorocode.tendertracker.dao.dto.UserRegistrationDTO;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Controller
public class AuthController {

    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String showLogin(ServletRequest request, Model model) {
        Map<String, String[]> paramMap = request.getParameterMap();

        if (paramMap.containsKey("error"))
        {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Invalid username or password");
            model.addAttribute("alert", alert);
        }
        model.addAttribute("login", new LoginDTO());

        return "login";
    }

    @GetMapping("/register")
    public ModelAndView showUserRegistration() {
        return new ModelAndView("registerUser", "registration", new UserRegistrationDTO());
    }

    @PostMapping("/register")
    public String saveUserRegistration(@Valid @ModelAttribute("registration") UserRegistrationDTO form,
                                       BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {

        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            return "registerUser";
        } else if (!form.getPassword().equals(form.getConfirmPassword())) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Password Confirmation is not same as the password");
            model.addAttribute("alert", alert);
            return "registerUser";
        }

        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setContactNo(form.getContactNo());
        user.setPassword(form.getPassword());

        try {
            userService.create(user);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, ex.getMessage());
            model.addAttribute("alert", alert);
            return "registerUser";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Registration Successful");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/login";
    }
}
