package com.dev;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {

    private static final Logger logger = LogManager.getLogger(LogController.class);

    @CrossOrigin(originPatterns = {"*"})
    @GetMapping("check-logs")
    public ResponseEntity<Object> checkIndentStatus(HttpServletRequest request) throws InterruptedException {
        for(int i=0;i<100;i++) {
            Thread.sleep(1000);
            logger.info("This is an info log message.");
            logger.error("This is an error log message.");
        }
        return new ResponseEntity<>("Logging working", HttpStatus.OK);
    }

}
