package com.degla.utils;

import com.degla.db.models.RoleEO;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * Created by snouto on 16/10/15.
 */
public class CustomRoleVoter implements AccessDecisionVoter {

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object o, Collection collection) {
        return ACCESS_GRANTED;
    }

    private int decideOn(String url , Authentication authentication)
    {
        return ACCESS_DENIED;
    }

    @Override
    public boolean supports(Class aClass) {
        return true;
    }
}
