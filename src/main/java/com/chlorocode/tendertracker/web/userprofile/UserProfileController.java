package com.chlorocode.tendertracker.web.userprofile;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.TenderClarificationDTO;
import com.chlorocode.tendertracker.dao.dto.UserProfileDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

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

        UserProfileDTO usrDto = new UserProfileDTO();
        usrDto.setContactNumber(usr.getContactNo());
        usrDto.setName(usr.getName());
        usrDto.setEmail(usr.getEmail());
        usrDto.setId(usr.getId());

        model.addAttribute("userprofile",usrDto);
        return "/admin/user/userProfile";
    }

    @PostMapping("/user/profile/update")
    public  String updateUserProfilePage(ModelMap model,@Valid UserProfileDTO form,
                                         @RequestParam("userId") int userId){

        User usr = userService.findById(userId);
        usr.setEmail(form.getEmail());
        usr.setContactNo(form.getContactNumber());
        usr.setLastUpdatedBy(userId);
        usr.setLastUpdatedDate(new Date());
        usr = userService.updateUserProfile(usr);

        if(usr==null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, "Failed to update user profile");
            model.addAttribute("alert", alert);
            UserProfileDTO usrDto = new UserProfileDTO();
            model.addAttribute("userprofile",usrDto);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Update user profile successfull");
            model.addAttribute("alert", alert);

            UserProfileDTO usrDto = new UserProfileDTO();
            usrDto.setContactNumber(usr.getContactNo());
            usrDto.setName(usr.getName());
            usrDto.setEmail(usr.getEmail());
            usrDto.setId(usr.getId());

            model.addAttribute("userprofile",usrDto);
        }
        return "/admin/user/userProfile";
    }
}
