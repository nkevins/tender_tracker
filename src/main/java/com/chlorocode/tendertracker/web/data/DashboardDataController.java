package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.dto.TenderStatusStatisticDTO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

@RestController
public class DashboardDataController {

    private TenderDAO tenderDAO;

    @Autowired
    public DashboardDataController(TenderDAO tenderDAO) {
        this.tenderDAO = tenderDAO;
    }

    @RequestMapping(value = "/admin/data/dashboard/tenderStatusStatistic", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<TenderStatusStatisticDTO> getTenders() {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        List<TenderStatusStatisticDTO> statistic = new LinkedList<>();

        List<Object[]> result = tenderDAO.getTenderStatusStatisticByCompany(companyId);
        for (Object[] obj : result) {
            statistic.add(new TenderStatusStatisticDTO((String) obj[0], ((BigInteger) obj[1]).intValue()));
        }

        return statistic;
    }
}
