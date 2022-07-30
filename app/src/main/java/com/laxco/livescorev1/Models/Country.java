package com.laxco.livescorev1.Models;

public class Country {
    String code,name,flag;

    public Country(String code, String name, String flag) {
        this.code = code;
        this.name = name;
        this.flag = flag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
