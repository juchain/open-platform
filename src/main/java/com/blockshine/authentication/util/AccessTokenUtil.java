package com.blockshine.authentication.util;


import com.blockshine.common.constant.CodeConstant;
import com.blockshine.common.exception.BusinessException;
import com.blockshine.common.util.*;

import java.io.UnsupportedEncodingException;

/**
 * AccessToken工具生成类
 * @author maxiaodong
 */
public class AccessTokenUtil {

    public static String generateToken(String appId, String appSecret){

        String token = CryptUtil.getSHA256Str(MD5Utils.encrypt(appId,appSecret + RandomUtil.getRandomCode()));

        try {
            String returnStr =  Base64Utils.encode(token);
            if(returnStr.length() >30){
                returnStr = returnStr.substring(0, 29);
            }

            return returnStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            BusinessException businessException = new BusinessException(e.getMessage(), CodeConstant.INTERAL_ERROR);
            throw businessException;

        }
    }

    public static void main(String[] args) {
        System.out.println("BM14__KJXJUZZBRXDPADBA".length());
    }


}
