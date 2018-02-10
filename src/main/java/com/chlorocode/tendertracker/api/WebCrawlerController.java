package com.chlorocode.tendertracker.api;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Public REST API controller to be consumed by tender crawler.
 * This REST API is secured by basic HTTP authentication
 */
@RestController
@RequestMapping("/restapi")
public class WebCrawlerController {

    @Autowired
    private LongProcess longProcess;

    @Value("${crawler.username}")
    private String crawlerUsername;

    @Value("${crawler.password}")
    private String crawlerPassword;

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    /**
     * This method is used to display test message for testing purpose.
     *
     * @param name text to  be displayed
     * @return String
     */
    @RequestMapping(path = "/greeting", method = RequestMethod.GET)
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(String.valueOf(counter.incrementAndGet()),
                String.format(template, name));
    }

    /**
     * This method is used to save external tender.
     *
     * @param tenders list of external tenders to be saved
     * @param request HttpServletRequest
     * @return ResponseEntity
     */
    @RequestMapping(path = "/tenders", method = RequestMethod.POST)
    public ResponseEntity tenders(@RequestBody List<ExternalTender> tenders, HttpServletRequest request) {
        TTLogger.debug(this.getClass().getName(),"*******************WebCrawlerController" + this.toString() + "*********************");

        // Validate authentication
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);

            if (values.length != 2 || !values[0].equals(crawlerUsername) || !values[1].equals(crawlerPassword)) {
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        // Proccess the request
        try {
            longProcess.createExternalTenderList(tenders);
        } catch (Exception ex) {
            TTLogger.error(this.getClass().getName(), "Error when saving External Tender", ex);
            ResponseEntity responseEntity = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }

        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        return responseEntity;
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