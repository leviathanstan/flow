package com.rdc.zrj.flow.hystrixcloud.controller;

import com.rdc.zrj.flow.hystrixcloud.service.UserService;
import com.rdc.zrj.flow.hystrixcloud.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leviathanstan
 * @date 04/29/2020 15:27
 */
@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/get")
    public String getUser(int option) {
        UserContextHolder.userContext.set("i am a header");
        switch (option) {
            case 1:
                return userService.getUser1(3);
            case 2:
                return userService.getUser2();
            case 3:
                return userService.getUser3();
            case 4:
                return userService.getUser4();
            case 5:
                return userService.getUser5();
            case 6:
                for (int i = 0; i < 5; i++) {
                    userService.getCache1(3);
                }
                break;
            case 7:
                for (int i = 0; i < 5; i++) {
                    userService.getCache2(3);
                }
                break;
            case 8:
                return userService.getUser7();
            default:
        }
        return "success";
    }
}
