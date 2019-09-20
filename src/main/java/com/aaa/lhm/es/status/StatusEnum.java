package com.aaa.lhm.es.status;

public enum StatusEnum {
    OPRATION_SUCCESS("code","200","msg","操作成功"),
    OPRATION_FAILED("code","400","msg","操作失败"),
    EXIST("code","101", "msg","数据存在"),
    NOT_EXIST("code","402","msg", "数据不存在");

    StatusEnum(String codeName,String code, String msgName,String msg) {
        this.codeName = codeName;
        this.code = code;
        this.msgName = msgName;
        this.msg = msg;
    }

    private String codeName;
    private String code;
    private String msgName;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgName() {
        return msgName;
    }

    public void setMsgName(String msgName) {
        this.msgName = msgName;
    }
}
