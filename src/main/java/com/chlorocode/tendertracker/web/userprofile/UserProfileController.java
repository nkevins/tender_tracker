package com.chlorocode.tendertracker.web.userprofile;

import com.chlorocode.tendertracker.dao.dto.AlertDTO;
import com.chlorocode.tendertracker.dao.dto.ChangePasswordDTO;
import com.chlorocode.tendertracker.dao.dto.UserProfileDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.CompanyService;
import com.chlorocode.tendertracker.service.TenderSubscriptionService;
import com.chlorocode.tendertracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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
    private CodeValueService codeValueService;
    private CompanyService companyService;
    private TenderSubscriptionService tenderSubscriptionService;

    @Autowired
    public UserProfileController(UserService userService, CodeValueService codeValueService
            , CompanyService companyService, TenderSubscriptionService tenderSubscriptionService)
    {
        this.userService = userService;
        this.codeValueService = codeValueService;
        this.companyService = companyService;
        this.tenderSubscriptionService = tenderSubscriptionService;
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
        List<TenderBookmark> lstBokkmark = tenderSubscriptionService.findTenderBookmarkByUserId(usr.getId());

        List<Company> lstCompany = companyService.findCompanyByCreatedBy(currentUser.getId());

        ChangePasswordDTO pwd = new ChangePasswordDTO();
        model.addAttribute("userprofile",usrDto);
        model.addAttribute("passwordDto",pwd);
        model.addAttribute("bookmarkTenderList",lstBokkmark);
        model.addAttribute("companyList",lstCompany);
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

        List<Company> lstCompany = companyService.findCompanyByCreatedBy(userId);
        model.addAttribute("companyList",lstCompany);
        List<TenderBookmark> lstBokkmark = tenderSubscriptionService.findTenderBookmarkByUserId(userId);
        model.addAttribute("bookmarkTenderList",lstBokkmark);
        ChangePasswordDTO pwd = new ChangePasswordDTO();
        model.addAttribute("passwordDto",pwd);
        return "userProfile";
    }

    @PostMapping("/user/profile/removeBookmark")
    public String removeTenderBookmark(ModelMap model,
                                       @RequestParam("tenderId") int tenderId){

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        tenderSubscriptionService.removeTenderBookmark(tenderId, currentUser.getUser().getId());

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

        List<TenderBookmark> lstBokkmark = tenderSubscriptionService.findTenderBookmarkByUserId(usr.getId());
        List<Company> lstCompany = companyService.findCompanyByCreatedBy(currentUser.getId());
        model.addAttribute("companyList",lstCompany);

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
        List<TenderBookmark> lstBokkmark = tenderSubscriptionService.findTenderBookmarkByUserId(userId);

        List<Company> lstCompany = companyService.findCompanyByCreatedBy(userId);
        model.addAttribute("companyList",lstCompany);

        model.addAttribute("bookmarkTenderList",lstBokkmark);
        model.addAttribute("userprofile",usrDto);
        model.addAttribute("passwordDto",pwd);
        return "userProfile";
    }

}
