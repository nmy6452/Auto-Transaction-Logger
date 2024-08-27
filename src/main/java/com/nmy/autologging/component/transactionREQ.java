package com.nmy.autologging.component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Slf4j
public class transactionREQ {
    private String host;
    private String path;

    private String query;

    private String method;

    private Map<String, String> form;
    private Map<String, String>  header;
    private String requestBody;

    public void consoleLog(){
        if (log.isDebugEnabled()){
            log.debug("Request host: {}", host);
            log.debug("Request path: {}", path);
            log.debug("Request query: {}", query);
            log.debug("Request method: {}", method);
            log.debug("Request form: {}", form.toString());
            log.debug("Request header: {}", header.toString());
            log.debug("Request requestBody: {}", requestBody);
        }
    }

}
