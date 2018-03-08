package com.blockshine.common.dto;

import com.blockshine.common.util.R;

import java.io.Serializable;


/**
 *
 * 封装common属性
 * @author maxiaodong
 */
public abstract class BaseDTO implements Serializable {
    private static final long serialVersionUID = -2780722731723741157L;

    private R r;

    public R getR() {
        return r;
    }

    public void setR(R r) {
        this.r = r;
    }

    @Override
    public String toString() {
        return "BaseDTO{" +
                "r=" + r +
                '}';
    }
}
