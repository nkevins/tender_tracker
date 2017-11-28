package com.chlorocode.tendertracker.api;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kyaw Min Thu on 4/30/2017.
 */
@RestController
@RequestMapping("/restapi")
public class WebCrawlerController {

    @Autowired
    private LongProcess longProcess;

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(path = "/greeting", method = RequestMethod.GET)
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(String.valueOf(counter.incrementAndGet()),
                String.format(template, name));
    }

    @RequestMapping(path = "/tenders", method = RequestMethod.POST)
    public String tenders(@RequestBody List<ExternalTender> tenders) {
        TTLogger.debug(this.getClass().getName(),"*******************WebCrawlerController" + this.toString() + "*********************");
        longProcess.createExternalTenderList(tenders);
        return "success";
    }

    public class Greeting implements Serializable {

        private final String id;
        private final String content;

        public Greeting(String id, String content) {
            this.id = id;
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "Greeting{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

}