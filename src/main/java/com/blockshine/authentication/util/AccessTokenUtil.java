package com.blockshine.authentication.util;


import com.blockshine.common.util.DateUtils;
import com.blockshine.common.util.MD5Utils;
import com.blockshine.common.util.RandomUtil;

/**
 * AccessToken工具生成类
 * @author maxiaodong
 */
public class AccessTokenUtil {

    public static String generateToken(String appId, String appSecret){

        String token = MD5Utils.encrypt(appId,appSecret + RandomUtil.getRandomCode());

        return token;
    }

    public static void main(String[] args) {
        String appId = "12300";
        String appSecret = "";
    }


}
