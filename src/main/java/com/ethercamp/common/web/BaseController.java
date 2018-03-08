package com.ethercamp.common.web;

import com.ethercamp.common.exception.BusinessException;
import com.ethercamp.common.util.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


/**
 * 处理业务上的基本异常
 */
@RestController
public abstract class BaseController {


    @ExceptionHandler
    public R handleBDException(BusinessException e) {
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());
        return r;
    }

}
