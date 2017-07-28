package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.service.CodeValueService;
import com.chlorocode.tendertracker.service.S3Wrapper;
import com.chlorocode.tendertracker.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TenderPublicController {

    private TenderService tenderService;
    private CodeValueService codeValueService;
    private S3Wrapper s3Wrapper;

    @Autowired
    public TenderPublicController(TenderService tenderService, CodeValueService codeValueService, S3Wrapper s3Wrapper) {
        this.tenderService = tenderService;
        this.codeValueService = codeValueService;
        this.s3Wrapper = s3Wrapper;
    }

    @GetMapping("/tender/{id}")
    public String showTenderDetails(@PathVariable(value="id") Integer id, ModelMap model) {
        Tender tender = tenderService.findById(id);
        if (tender == null) {
            return "redirect:/";
        }

        model.addAttribute("tender", tender);
        model.addAttribute("codeValueService", codeValueService);
        model.addAttribute("s3Service", s3Wrapper);
        return "tenderDetails";
    }
}
