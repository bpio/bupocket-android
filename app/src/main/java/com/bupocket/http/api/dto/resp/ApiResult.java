package com.bupocket.http.api.dto.resp;

import com.bupocket.enums.ExceptionEnum;

public class ApiResult<T> {
    String errCode;
    String msg;
    T data;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



    public boolean isSuccess(){
        if (ExceptionEnum.SUCCESS.getCode().equals(errCode)) {
            return true;
        }
        return false;
    }
}
