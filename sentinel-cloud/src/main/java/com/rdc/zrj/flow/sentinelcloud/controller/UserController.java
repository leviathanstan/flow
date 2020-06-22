package com.rdc.zrj.flow.sentinelcloud.controller;

import com.rdc.zrj.flow.sentinelcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leviathanstan
 * @date 05/06/2020 17:59
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/get")
    public String get(long id) {
        return userService.get(id);
    }

    @GetMapping(value = "/getByDegrade")
    public String getByDegrade() {
        return userService.getByDegrade();
    }
}
