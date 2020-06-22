package com.rdc.zrj.flow.hystrixgateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leviathanstan
 * @date 05/07/2020 11:35
 */
@RestController
public class FallbackHandler {

    @GetMapping(value = "/fallback")
    public String fallback(Throwable throwable) {
        System.out.println(throwable.getMessage());
        return "====error====";
    }
}
