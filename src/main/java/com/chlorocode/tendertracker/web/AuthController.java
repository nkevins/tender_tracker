package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.dto.*;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CompanyService;
import com.chlorocode.tendertracker.service.UserRoleService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controller for authentication and administration.
 */
@Controller
public class AuthController {

    private UserService userService;
    private UserRoleService userRoleService;
    private CompanyService companyService;
    private CodeValueService codeValueService;

    /**
     * Constructor
     *
     * @param userService UserService
     * @param companyService CompanyService
     * @param codeValueService CodeValueService
     * @param userRoleService UserRoleService
     */
    @Autowired
    public AuthController(UserService userService, CompanyService companyService,
                          CodeValueService codeValueService, UserRoleService userRoleService) {
        this.userService = userService;
        this.companyService = companyService;
        this.codeValueService = codeValueService;
        this.userRoleService = userRoleService;
    }

    /**
     * This method is used to show login screen.
     *
     * @param request ServletRequest
     * @param model Model
     * @return String
     */
    @RequestMapping("/login")
    public String showLogin(ServletRequest request, Model model) {
        model.addAttribute("login", new LoginDTO());

        return "login";
    }

    /**
     * This method is used to show user registration screen.
     *
     * @param model ModelMap
     * @return ModelAndView
     */
    @GetMapping("/register")
    public ModelAndView showUserRegistration(ModelMap model) {
        model.addAttribute("IdType",codeValueService.getByType("id_type"));
        return new ModelAndView("registerUser", "registration", new UserRegistrationDTO());

    }

    /**
     * This method is used to register user.
     *
     * @param form UserRegistrationDTO
     * @param result BindingResult
     * @param redirectAttrs RedirectAttributes
     * @param model ModelMap
     * @return String
     * @see UserRegistrationDTO
     */
    @PostMapping("/register")
    public String saveUserRegistration(@Valid @ModelAttribute("registration") UserRegistrationDTO form,
                                       BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {

        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("IdType",codeValueService.getByType("id_type"));
            return "registerUser";
        } else if (!form.getPassword().equals(form.getConfirmPassword())) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Password Confirmation is not same as the password");
            model.addAttribute("alert", alert);
            model.addAttribute("IdType",codeValueService.getByType("id_type"));
            return "registerUser";
        }

        if(form.getIdType() == 1){
            if(!userService.isNRICValid(form.getIdNo())){
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                        "Invalid NRIC No.");
                model.addAttribute("alert", alert);
                model.addAttribute("IdType",codeValueService.getByType("id_type"));
                return "registerUser";
            }
        }else if (form.getIdType() == 2){
            if(!userService.isFINValid(form.getIdNo())){
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                        "Invalid FIN No.");
                model.addAttribute("alert", alert);
                model.addAttribute("IdType",codeValueService.getByType("id_type"));
                return "registerUser";
            }
        }

        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setContactNo(form.getContactNo());
        user.setPassword(form.getPassword());
        user.setIdType(form.getIdType());
        user.setIdNo(form.getIdNo());

        try {
            userService.create(user);
        } catch (ApplicationException ex) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, ex.getMessage());
            model.addAttribute("alert", alert);
            model.addAttribute("IdType",codeValueService.getByType("id_type"));
            return "registerUser";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Registration Successful");
        redirectAttrs.addFlashAttribute("alert", alert);
        return "redirect:/login";
    }

    /**
     * This method is used to show select company screen.
     *
     * @return ModelAndView
     */
    @GetMapping("/selectCompany")
    public ModelAndView showSelectCompany() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();
        List<Company> administeredCompanies = usr.getCompanyAdministered();

        return new ModelAndView("selectCompany", "company", administeredCompanies);
    }

    /**
     * This method is used to select company for managing.
     *
     * @param companyId unique identifier of the company
     * @return String
     */
    @PostMapping("/selectCompany")
    public String selectCompanyToManage(@RequestParam("companyId") String companyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();

        usr.setSelectedCompany(companyService.findById(Integer.parseInt(companyId)));
        usr.setNeedToSelectCompany(false);

        List<Role> roles = userRoleService.findCompanyUserRole(usr.getId(), Integer.parseInt(companyId));
        String role = "ROLE_USER";
        for (Role r : roles) {
            role += ",ROLE_" + r.getName();
        }

        Authentication newAuth = new UsernamePasswordAuthenticationToken(usr, null, AuthorityUtils.commaSeparatedStringToAuthorityList(role));
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/";
    }

    /**
     * This method used to show forgot password screen.
     *
     * @param request ServletRequest
     * @param model Model
     * @return String
     */
    @RequestMapping("/forgotPassword")
    public String forgotPassword(ServletRequest request, Model model) {
        Map<String, String[]> paramMap = request.getParameterMap();
        model.addAttribute("forgotPassword", new ForgotPasswordDTO());

        return "forgotPassword";
    }

    /**
     * This method used to send PIN for identifying user email.
     *
     * @param form ForgotPasswordDTO
     * @param result BindingResult
     * @param redirectAttrs RedirectAttributes
     * @param model ModelMap
     * @return String
     * @see ForgotPasswordDTO
     */
    @RequestMapping("/sendPIN")
    public String sendPIN(@Valid @ModelAttribute("forgotPassword") ForgotPasswordDTO form,
                          BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {
        String errMsg = null;
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
            model.addAttribute("forgotPassword", form);
            return "forgotPassword";
        } else if ((errMsg = userService.sendPasswordResetPIN(form.getEmail())) != null) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, errMsg);
            model.addAttribute("alert", alert);
            model.addAttribute("forgotPassword", form);
            return "forgotPassword";
        }

        AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                "Password reset PIN email has successfully sent to '"
                        + form.getEmail() + "'. Please follow the instruction in the email.");
        model.addAttribute("alert", alert);

        return "forgotPassword";
    }

    /**
     * This method is used to reset the user password.
     *
     * @param email email address of the user
     * @param pin pin number of the user for identifying user
     * @param request ServletRequest
     * @param model Model
     * @return String
     */
    @GetMapping("/resetPassword/{email}/{pin}")
    public String resetPassword(@PathVariable(value="email") String email, @PathVariable(value="pin") String pin, ServletRequest request, Model model) {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        if ((email = email.trim()).isEmpty() || (pin = pin.trim()).isEmpty() || !userService.isPinValid(email, pin)) {
            // Error message. Don't show error message detail because of security.
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Password reset PIN is invalid.");
            model.addAttribute("alert", alert);
        } else {
            // Put email into DTO for maintain as hidden field.
            dto.setEmail(email);
        }
        model.addAttribute("changePassword", dto);

        return "changePassword";
    }

    /**
     * This method is used to change the user password.
     *
     * @param form ChangePasswordDTO
     * @param result BindingResult
     * @param redirectAttrs RedirectAttributes
     * @param model ModelMap
     * @return String
     * @see ChangePasswordDTO
     */
    @RequestMapping("/changePassword")
    public String changePassword(@Valid @ModelAttribute("changePassword") ChangePasswordDTO form
            , BindingResult result, RedirectAttributes redirectAttrs, ModelMap model) {
        if (result.hasErrors()) {
            AlertDTO alert = new AlertDTO(result.getAllErrors());
            model.addAttribute("alert", alert);
        } else if (!form.getPassword().equals(form.getConfirmPassword())) {
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "Password Confirmation is not same as the password");
            model.addAttribute("alert", alert);
        } else {
            if (userService.updatePassword(form.getEmail(), form.getPassword()) == null) {
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, "Failed to reset password.");
                model.addAttribute("alert", alert);
            } else {
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS,
                        "Password reset successfully.");
                model.addAttribute("alert", alert);
                form.setEmail(null);
            }
        }

        model.addAttribute("changePassword", form);
        return "changePassword";
    }
}
