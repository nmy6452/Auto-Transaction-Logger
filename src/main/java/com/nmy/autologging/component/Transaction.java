package com.nmy.autologging.component;

import com.nmy.autologging.config.AppConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

//TODO request scope로 변경
@Component
//@Scope(value = "request")
@Data
public class Transaction{
    AppConfig config;

    private transactionREQ request;
    private transactionRES response;

    public Transaction() {
        this.request = new transactionREQ();
        this.response = new transactionRES();
    }

    @Autowired
    public Transaction(AppConfig config) {
        this.config = config;
        this.request = new transactionREQ();
        this.response = new transactionRES();
    }

    public void consoleLog(){
        request.consoleLog();
//        response.consoleLog();
    }

    public void dbLog(){
        //TODO DB로깅이 켜져있는 경우 로깅 설정
    }
}
