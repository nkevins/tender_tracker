package com.chlorocode.tendertracker.api;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.service.ExternalTenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.List;
import java.util.concurrent.Future;

/**
 * This class is used to handle long process threading during external tender saving process.
 */
public class LongProcess {

    @Autowired
    private ExternalTenderService tenderWCService;

    /**
     * This method is used to save external tender asynchronously.
     *
     * @param tenders list of external tender to be saved
     * @return String
     */
    @Async
    public Future<String> createExternalTenderList(List<ExternalTender> tenders) {
        tenderWCService.createTenderWCList(tenders);
        return new AsyncResult<>("success");
    }

}
