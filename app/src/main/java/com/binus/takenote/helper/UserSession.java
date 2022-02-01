package com.binus.takenote.helper;

import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.hms.support.account.result.AuthAccount;

public class UserSession {
    private static UserSession instance;
    private AGConnectUser user;
    private AuthAccount account;

    private UserSession(){ }

    public static UserSession getInstance(){
        if(instance == null){
            instance = new UserSession();
        }

        return instance;
    }

    public AuthAccount getAccount() {
        return account;
    }

    public void setAccount(AuthAccount account) {
        this.account = account;
    }

    public AGConnectUser getUser() {
        return user;
    }

    public void setUser(AGConnectUser user) {
        this.user = user;
    }
}
