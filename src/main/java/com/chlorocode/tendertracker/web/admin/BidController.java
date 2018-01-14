package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.entity.Bid;
import com.chlorocode.tendertracker.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BidController {

    BidService bidService;

    @Autowired
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping("/admin/bid")
    public String showTenderPage() {
        return "admin/bid/bidView";
    }

    @GetMapping("/admin/bid/appeal/{id}")
    public String appealTender(@PathVariable(value="id") Integer id, ModelMap model) {
        Bid bid = bidService.findById(id);

        model.addAttribute("bid", bid);

        return "admin/bid/submitBidAppeal";
    }
}
