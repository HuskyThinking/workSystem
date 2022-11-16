/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2022 程序员十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package net.husky.config.handler;

import net.husky.config.annotation.TokenToAdminUser;
import net.husky.dao.NewBeeAdminUserTokenMapper;
import net.husky.entity.AdminUserToken;
import net.husky.util.NewBeeMallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class TokenToAdminUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private NewBeeAdminUserTokenMapper newBeeAdminUserTokenMapper;

    public TokenToAdminUserMethodArgumentResolver() {
    }


    // supportsParameter：用于判定是否需要处理该参数分解，返回true为需要，并会去调用下面的方法resolveArgument
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(TokenToAdminUser.class)) {
            return true;
        }
        return false;
    }

    // resolveArgument：真正用于处理参数分解的方法，返回的Object就是controller方法上的形参对象
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.getParameterAnnotation(TokenToAdminUser.class) instanceof TokenToAdminUser) {
            String token = webRequest.getHeader("token");
            if (null != token && !"".equals(token) && token.length() == 32) {
                AdminUserToken adminUserToken = newBeeAdminUserTokenMapper.selectByToken(token);
                if (adminUserToken == null) {
                    NewBeeMallException.fail("ADMIN_NOT_LOGIN_ERROR");
                } else if (adminUserToken.getExpireTime().getTime() <= System.currentTimeMillis()) {
                    NewBeeMallException.fail("ADMIN_TOKEN_EXPIRE_ERROR");
                }
                return adminUserToken;
            } else {
                NewBeeMallException.fail("ADMIN_NOT_LOGIN_ERROR");
            }
        }
        return null;
    }

}
