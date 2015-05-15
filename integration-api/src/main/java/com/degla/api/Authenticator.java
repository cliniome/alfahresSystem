package com.degla.api;

/**
 * This interface represents all the operations needed by an external authenticator that will be used to authenticate
 * users and return the result to AlFahres system
 * Created by snouto on 03/05/2015.
 */
public interface Authenticator {

    public boolean authenticate(String username , String password);
    public boolean isEnabled();
}
