package com.seaboxdata.authcdk.service;

import com.seaboxdata.authcdk.controller.AuthCDKController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthCDKServiceTest {

    @Autowired
    private AuthCDKController authCDKController;

    @Test
    public void generatorCDK() throws ParseException {
//        String generatorCDK = authCDKController.generatorCDK("132788977987096575", "3");
    }

}