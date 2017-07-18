package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenderServiceImpl implements TenderService {

    private TenderDAO tenderDAO;

    @Autowired
    public TenderServiceImpl(TenderDAO tenderDAO) {
        this.tenderDAO = tenderDAO;
    }

    @Override
    public Tender createTender(Tender t) {
        if (t.getOpenDate().after(t.getClosedDate())) {
            throw new ApplicationException("Tender Closing Date must be later than Tender Opening Date");
        }

        return tenderDAO.save(t);
    }
}
