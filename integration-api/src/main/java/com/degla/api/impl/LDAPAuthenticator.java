package com.degla.api.impl;


import com.degla.api.Authenticator;

/**
 * Created by snouto on 03/05/2015.
 */
public class LDAPAuthenticator implements Authenticator {

    private boolean enabled;


    @Override
    public boolean authenticate(String username, String password) {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
