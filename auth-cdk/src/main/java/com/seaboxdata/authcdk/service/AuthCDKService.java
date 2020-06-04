package com.seaboxdata.authcdk.service;

import com.seaboxdata.authcdk.controller.AuthCDKController;
import com.seaboxdata.authcdk.utils.AESUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author makaiyu
 * @date 2019/9/6 9:14
 */
@Service
public class AuthCDKService implements AuthCDKController {
    @Override
    public String generatorCDK(String userId, String tenantUseCount,String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(time);
        Long timestamp = date.getTime();

        return AESUtils.generatorPlatformActivityCode(userId, tenantUseCount, timestamp.toString());
    }
}
