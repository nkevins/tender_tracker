package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.UserRoleDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ResourceNotFoundException;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.UserRoleService;
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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class AdminController extends HttpServlet {

    private UserService userService;
    private CodeValueService codeValueService;
    private UserRoleService usrRoleSvc;
    private String className;

    @Autowired
    public AdminController(UserService userService,
                           CodeValueService codeValueService,
                           UserRoleService usrRoleSvc) {
        this.userService = userService;
        this.codeValueService = codeValueService;
        this.usrRoleSvc = usrRoleSvc;
        this.className = this.getClass().getName();
    }


    @GetMapping("/admin")
    public String showDashboardPage() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/userDetails")
    public String showUserDetails() {
        return "admin/user/userView";
    }

    @GetMapping("/admin/userList")
    public String showUserListDetails() {
        return "admin/user/userList";
    }

    @GetMapping("/admin/userList/{id}/{roleid}")
    public String displayUserRoleDetails(@PathVariable(value="id") Integer id,
                                         @PathVariable(value="roleid") Integer roleId,
                                         Model model,
                                         HttpServletRequest request){

        User usr = userService.findById(id);
        if(usr == null){
            //ToDo link to user role listing page
            TTLogger.error(className,"User is not found. User Id: " + id);
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "User is not found. User Id: " + id);
            model.addAttribute("alert", alert);
            return "redirect:/";
        }

        UserRole role = usrRoleSvc.findUserRoleById(roleId);
        if(role == null){
               //ToDo link to user role listing page
            TTLogger.error(className,"User role is not found. Role Id: " + id);
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "User role is not found. Role Id: " + roleId);
            model.addAttribute("alert", alert);
            return "redirect:/";
        }

        UserRoleDTO roleDto = new UserRoleDTO();
        roleDto.setRoleId(roleId);
        roleDto.setRoleName(role.getRole().getName());
        model.addAttribute("userRoleDto", roleDto);

        model.addAttribute("reg", usr);
        model.addAttribute("user", new UserRoleDTO());
        List<CodeValue> userTypeCombo = codeValueService.getByType("user_type");
        model.addAttribute("userType", userTypeCombo);
        HttpSession sess = request.getSession();
        
        return "admin/user/updateUserRole";
    }

    @PostMapping("/admin/user/updateRole")
    public String updateUserRole(@RequestParam("action") String action,
                                 @RequestParam("roleId") int roleId,
                                 @Valid @ModelAttribute("user") UserRoleDTO form, Model model,
                                 RedirectAttributes redirectAttrs){
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(action.equals("update")){
            boolean result = userService.updateUserRole(roleId,form.getId(),usr.getId());
            if(result){

            }else{
                TTLogger.error(className,"failed to update user role");
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                        "failed to update user role");
                model.addAttribute("alert", alert);
            }
        }else{
            boolean result = userService.deleteUserRole(roleId);
            if(result){

            }else{
                TTLogger.error(className,"failed to delete user role");
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                        "failed to delete user role");
                model.addAttribute("alert", alert);
            }
        }

        return "redirect:/";
    }
    @GetMapping("/admin/userDetails/{id}")
    public String displayUserDetail(@PathVariable(value="id") Integer id, Model model,
                                    HttpServletRequest request) {
        User user = userService.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        } else {
            model.addAttribute("user", new UserRoleDTO());
            model.addAttribute("reg", user);
            List<CodeValue> userTypeCombo = codeValueService.getByType("user_type");
            model.addAttribute("userType", userTypeCombo);
            HttpSession sess = request.getSession();
            sess.setAttribute("loginUser",user);
            sess.setAttribute("userCombo",userTypeCombo);
            return "admin/user/assignUerRole";
        }

    }

    @PostMapping("/admin/user/assignRole")
    public String saveCompanyRegistration(@RequestParam("userid") int id,
                                          @Valid @ModelAttribute("user") UserRoleDTO form,
                                          BindingResult result, RedirectAttributes redirectAttrs,
                                          ModelMap model,HttpServletRequest request) {

        //UserRole userRole = new UserRole();

        RoleUser role = new RoleUser();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser usr = (CurrentUser) auth.getPrincipal();
        Integer roleId = userService.findUserRoleId(id,usr.getSelectedCompany().getId(),form.getId());

        if(roleId != null && roleId > 0){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER,
                    "User already exist with this role. Please assign other role to this user");
            model.addAttribute("alert", alert);
            HttpSession sess = request.getSession();
            if(sess.getAttribute("loginUser") != null && sess.getAttribute("userCombo") != null){
                User u = (User) sess.getAttribute("loginUser");
                model.addAttribute("reg", u);
                model.addAttribute("userType", sess.getAttribute("userCombo"));
                return "admin/user/assignUerRole";
            }else{
                //ToDo: return to access denied page
                return "redirect:/";
            }

        }
        role.setRoleId(form.getId());
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
