package com.how2java.tmall.realm;

import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.UserService;
import org.apache.coyote.http2.ByteUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class JpaRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = token.getPrincipal().toString();
        User user = userService.getName(username);
        String password = user.getPassword();
        String salt = user.getSalt();
        SimpleAuthenticationInfo simpleAuthorizationInfo = new SimpleAuthenticationInfo(username,password, ByteSource.Util.bytes(salt),getName());
        return simpleAuthorizationInfo;
    }
}
