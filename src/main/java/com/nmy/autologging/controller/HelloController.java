package com.nmy.autologging.controller;

import com.nmy.autologging.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class HelloController {

    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    public String hello(
            @RequestParam String name
    ){
        log.debug("name:: {}", name);

        return name+"hello";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/hello")
    public String helloPost(
            @RequestParam String name
    ){
        log.debug("name:: {}", name);
        return name+"hello";
    }
}
