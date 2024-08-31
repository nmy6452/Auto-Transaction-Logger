package com.nmy.autologging.component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Slf4j
public class transactionRES {

    private Map<String, String> form;
    private Map<String, String>  header;
    private String responseBody;

    public void consoleLog(){
        if (log.isDebugEnabled()){
            log.debug("Response form: {}", form);
            log.debug("Response header: {}", header);
            log.debug("Response requestBody: {}", responseBody);
        }
    }

    public void dbLog(){
        if (log.isDebugEnabled()){
            log.debug("Response form: {}", form);
            log.debug("Response header: {}", header);
            log.debug("Response requestBody: {}", responseBody);
        }
    }
}
