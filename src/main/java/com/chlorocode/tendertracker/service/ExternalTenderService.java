package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.TenderSearchDTO;
import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Kyaw Min Thu on 5/1/2017.
 */
public interface ExternalTenderService {
    public String createTenderWCList(List<ExternalTender> tenders);
    public Page<ExternalTender> listAllByPage(Pageable pageable);
    public Page<ExternalTender> searchTender(TenderSearchDTO searchDTO, Pageable pageable);
}
