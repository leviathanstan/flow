package com.rdc.zrj.flow.hystrixdynamic.controller;

import com.rdc.zrj.flow.hystrixdynamic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leviathanstan
 * @date 04/29/2020 15:27
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/get")
    public String getUser() {
        return userService.getUser();
    }

    @GetMapping(value = "/change")
    public String change() {
        return userService.change();
    }
}
