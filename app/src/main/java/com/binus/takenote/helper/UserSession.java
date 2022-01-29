package com.binus.takenote.helper;

import com.huawei.agconnect.auth.AGConnectUser;

public class UserSession {
    private static UserSession instance;
    private AGConnectUser user;

    private UserSession(){ }

    public static UserSession getInstance(){
        if(instance == null){
            instance = new UserSession();
        }

        return instance;
    }

    public AGConnectUser getUser() {
        return user;
    }

    public void setUser(AGConnectUser user) {
        this.user = user;
    }
}
