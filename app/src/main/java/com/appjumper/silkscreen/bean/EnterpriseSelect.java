package com.appjumper.silkscreen.bean;

import java.util.List;

/**
 * Created by Administrator on 2016-12-12.
 */
public class EnterpriseSelect {
    List<Enterprise> auth;
    List<Enterprise> noauth;

    public List<Enterprise> getAuth() {
        return auth;
    }

    public void setAuth(List<Enterprise> auth) {
        this.auth = auth;
    }

    public List<Enterprise> getNoauth() {
        return noauth;
    }

    public void setNoauth(List<Enterprise> noauth) {
        this.noauth = noauth;
    }
}
