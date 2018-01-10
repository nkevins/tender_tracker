package com.chlorocode.tendertracker.api;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.service.ExternalTenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Kyaw Min Thu on 5/1/2017.
 */
public class LongProcess {
    @Autowired
    private ExternalTenderService tenderWCService;

    @Async
    public Future<String> createExternalTenderList(List<ExternalTender> tenders) {
        tenderWCService.createTenderWCList(tenders);
        return new AsyncResult<>("success");
    }

}
