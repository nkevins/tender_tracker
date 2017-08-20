package com.chlorocode.tendertracker.web.userprofile;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.ChangePasswordDTO;
import com.chlorocode.tendertracker.dao.dto.TenderClarificationDTO;
import com.chlorocode.tendertracker.dao.dto.UserProfileDTO;
import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.TenderBookmark;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.TenderService;
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
import java.util.List;

/**
 * Created by andy on 9/8/2017.
 */
@Controller
public class UserProfileController {
    private UserService userService;
    private TenderService tenderService;
    private CodeValueService codeValueService;

    @Autowired
    public UserProfileController(UserService userService,TenderService tenderService, CodeValueService codeValueService)
    {
        this.userService = userService;
        this.tenderService = tenderService;
        this.codeValueService = codeValueService;
    }

    @GetMapping("/user/profile")
    public  String showUserProfilePage(ModelMap model){

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, "Please login to view user profile.");
            model.addAttribute("alert", alert);
            return "redirect:/";
        }
        List<CodeValue> idTypeValue = codeValueService.getByType("id_type");
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

        for(int i = 0; i < idTypeValue.size(); i++){
            CodeValue code = idTypeValue.get(i);
            if(code.getCode() == usr.getIdType()){
                usrDto.setIdType(code.getDescription());
                break;
            }
        }

        usrDto.setIdNo(usr.getIdNo());
        List<TenderBookmark> lstBokkmark = tenderService.findTenderBookmarkByUserId(usr.getId());

        ChangePasswordDTO pwd = new ChangePasswordDTO();
        model.addAttribute("userprofile",usrDto);
        model.addAttribute("passwordDto",pwd);
        model.addAttribute("bookmarkTenderList",lstBokkmark);
        return "userProfile";
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
            usr = userService.findById(userId);
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, "Failed to update user profile");
            model.addAttribute("alert", alert);
        }else{
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Update user profile successfull");
            model.addAttribute("alert", alert);
        }

        UserProfileDTO usrDto = new UserProfileDTO();
        usrDto.setContactNumber(usr.getContactNo());
        usrDto.setName(usr.getName());
        usrDto.setEmail(usr.getEmail());
        usrDto.setId(usr.getId());
        usrDto.setIdNo(usr.getIdNo());
        List<CodeValue> idTypeValue = codeValueService.getByType("id_type");

        for(int i = 0; i < idTypeValue.size(); i++){
            CodeValue code = idTypeValue.get(i);
            if(code.getCode() == usr.getIdType()){
                usrDto.setIdType(code.getDescription());
                break;
            }
        }
        model.addAttribute("userprofile",usrDto);

        List<TenderBookmark> lstBokkmark = tenderService.findTenderBookmarkByUserId(userId);
        model.addAttribute("bookmarkTenderList",lstBokkmark);
        ChangePasswordDTO pwd = new ChangePasswordDTO();
        model.addAttribute("passwordDto",pwd);
        return "userProfile";
    }

    @PostMapping("/user/profile/removeBookmark")
    public String removeTenderBookmark(ModelMap model,
                                       @RequestParam("tenderId") int tenderId){

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        tenderService.removeTenderBookmark(tenderId, currentUser.getUser().getId());

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
        usrDto.setIdNo(usr.getIdNo());

        List<CodeValue> idTypeValue = codeValueService.getByType("id_type");

        for(int i = 0; i < idTypeValue.size(); i++){
            CodeValue code = idTypeValue.get(i);
            if(code.getCode() == usr.getIdType()){
                usrDto.setIdType(code.getDescription());
                break;
            }
        }

        List<TenderBookmark> lstBokkmark = tenderService.findTenderBookmarkByUserId(usr.getId());

        ChangePasswordDTO pwd = new ChangePasswordDTO();
        model.addAttribute("userprofile",usrDto);
        model.addAttribute("passwordDto",pwd);
        model.addAttribute("bookmarkTenderList",lstBokkmark);
        return "userProfile";
    }
    @PostMapping("/user/password/update")
    public String updateUserPassword(ModelMap model,@Valid ChangePasswordDTO form,
                                     @RequestParam("userId") int userId,  @RequestParam("email") String email){

        if(!form.getPassword().equalsIgnoreCase(form.getConfirmPassword())){
            AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, "New password does not match");
            model.addAttribute("alert", alert);
        }else{
            User usr = userService.updatePassword(email,form.getPassword());
            if(usr == null){
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.DANGER, "Failed to update user password");
                model.addAttribute("alert", alert);
            }else{
                AlertDTO alert = new AlertDTO(AlertDTO.AlertType.SUCCESS, "Update password successfull");
                model.addAttribute("alert", alert);
            }
        }

        User usr = userService.findById(userId);
        UserProfileDTO usrDto = new UserProfileDTO();
        usrDto.setContactNumber(usr.getContactNo());
        usrDto.setName(usr.getName());
        usrDto.setEmail(usr.getEmail());
        usrDto.setId(usr.getId());
        usrDto.setIdNo(usr.getIdNo());

        List<CodeValue> idTypeValue = codeValueService.getByType("id_type");

        for(int i = 0; i < idTypeValue.size(); i++){
            CodeValue code = idTypeValue.get(i);
            if(code.getCode() == usr.getIdType()){
                usrDto.setIdType(code.getDescription());
                break;
            }
        }

        ChangePasswordDTO pwd = new ChangePasswordDTO();
        List<TenderBookmark> lstBokkmark = tenderService.findTenderBookmarkByUserId(userId);

        model.addAttribute("bookmarkTenderList",lstBokkmark);
        model.addAttribute("userprofile",usrDto);
        model.addAttribute("passwordDto",pwd);
        return "userProfile";
    }

}
