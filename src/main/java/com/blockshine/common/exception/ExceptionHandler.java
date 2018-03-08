package com.blockshine.common.exception;


import com.blockshine.common.util.BDException;
import com.blockshine.common.util.R;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 自定义异常处理器
 * @author maxiaodong
 */

@RestControllerAdvice
public class ExceptionHandler {

    /**
     * 自定义异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public R handleBDException(BusinessException e) {
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());

        return r;
    }


}
