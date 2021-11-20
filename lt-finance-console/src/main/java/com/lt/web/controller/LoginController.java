package com.lt.web.controller;

import com.lt.web.service.UserService;
import com.lt.view.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaijf
 * @description
 * @date 2020/12/2
 */
@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/oauth/user/token")
    public ResultEntity<String> getUserTokenInfo(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password) {
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            log.info("用户名或密码为空");
            return null;
        }
        String token = userService.getUserTokenInfo(username,password);
        return ResultEntity.success(token);
    }
}
