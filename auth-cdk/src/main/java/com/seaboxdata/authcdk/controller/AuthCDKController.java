package com.seaboxdata.authcdk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * @author makaiyu
 * @date 2019/9/6 9:13
 */
@RequestMapping("/auth/cdk")
@RestController
public interface AuthCDKController {

    @GetMapping("/generator")
    String generatorCDK(String userId, String tenantUseCount,String time) throws ParseException;
}
