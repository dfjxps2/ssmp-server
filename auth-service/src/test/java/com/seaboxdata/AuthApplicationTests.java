package com.seaboxdata;

import com.seaboxdata.auth.server.AuthorizationApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthorizationApplication.class)
public class AuthApplicationTests {

    @Test
    public void contextLoads() {
    }

}
