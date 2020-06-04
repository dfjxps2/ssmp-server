package com.seaboxdata.auth.server.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;

/**
 * @author makaiyu
 * @date 2019/5/31 9:43
 */
public class NumberFormatUtils {

    /**
     * @param num
     * @return java.lang.String
     * @author makaiyu
     * @description 格式化数字 1 -> 001  701 -> 00701
     * @date 10:31 2019/5/31
     **/
    public static String numberFormat(Integer num) {
        DecimalFormat df = null;
        if ((num + "").length() == NumberUtils.INTEGER_ONE) {
            df = new DecimalFormat("000");
        } else if ((num + "").length() == NumberUtils.INTEGER_TWO + NumberUtils.INTEGER_ONE) {
            df = new DecimalFormat("00000");
        } else {
            df = new DecimalFormat("0");
        }

        return df.format(num);
    }

}
