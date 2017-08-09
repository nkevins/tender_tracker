package com.chlorocode.tendertracker.web.userprofile;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by andy on 9/8/2017.
 */
@Controller
public class UserProfileController {
    private UserService userService;

    @Autowired
    public UserProfileController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("/user/profile")
    public  String showUserProfilePage(ModelMap model){

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, "Please login to view user profile.");
            model.addAttribute("alert", alert);
            return "redirect:/";
        }

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User usr = userService.findById(currentUser.getId());
        if(usr == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, "invalid user profile found. user id " + currentUser.getId());
            model.addAttribute("alert", alert);
        }

        model.addAttribute("userprofile",usr);
        return "/admin/user/userProfile";
    }
}
