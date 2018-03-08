package com.blockshine.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;


/**
 * Base64工具类
 *
 * @author maxiaodong
 */
public class Base64Utils {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();


    public static String encode(String text) throws UnsupportedEncodingException {
        final byte[] textByte = text.getBytes("UTF-8");
        String encodedText = encoder.encodeToString(textByte);

        return encodedText;

    }

    public static String decode(String text) throws UnsupportedEncodingException {

        String encodedText = new String(decoder.decode(text), "UTF-8");

        return encodedText;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(encode(CryptUtil.getSHA256Str("maxiaodongxxxx")));
        System.out.println("ODM1NzIwZTU3ODJlOTQ5YjgxNGY5NmQ5NjA5ZTczMjdmY2RjNjBjODdmOWY4Mzc0MTkzZGQ0ZmM5MDE2ODhkZA==".length());
    }

}
