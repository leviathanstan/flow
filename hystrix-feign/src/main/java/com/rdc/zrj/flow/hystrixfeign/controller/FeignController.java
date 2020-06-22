package com.rdc.zrj.flow.hystrixfeign.controller;

import com.rdc.zrj.flow.hystrixfeign.service.FeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leviathanstan
 * @date 05/07/2020 10:20
 */
@RestController
@RequestMapping(value = "feign")
public class FeignController {

    @Autowired
    private FeignService feignService;

    @GetMapping(value = "/get")
    public String get() {
        return feignService.get();
    }
}
