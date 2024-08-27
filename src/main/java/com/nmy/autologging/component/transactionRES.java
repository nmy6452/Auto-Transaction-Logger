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
    private String host;
    private String path;

    private String query;

    private String method;

    private Map<String, String> form;
    private Map<String, String>  header;
    private String requestBody;
}
