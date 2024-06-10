# custom-logging-log4j2
Custom Log4j2 logging appender example in Spring boot 3

# Custom Log4j Appender
Feature - 
 * Send logs to http if server is online else dump them to log file
 * Once system is online the flush logs to http again

## To Generate Logs hit below api from any client
    http://localhost:8080/check-logs

