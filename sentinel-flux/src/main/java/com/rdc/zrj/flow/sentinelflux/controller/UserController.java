package com.rdc.zrj.flow.sentinelflux.controller;

import com.alibaba.csp.sentinel.adapter.reactor.SentinelReactorTransformer;
import com.rdc.zrj.flow.sentinelflux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author leviathanstan
 * @date 05/06/2020 17:35
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService fooService;

    @GetMapping("/single")
    public Mono<String> apiNormalSingle() {
        return fooService.emitSingle()
                // transform the publisher here.
                .transform(new SentinelReactorTransformer<>("demo_foo_normal_single"));
    }

    @GetMapping("/flux")
    public Flux<Integer> apiNormalFlux() {
        return fooService.emitMultiple()
                .transform(new SentinelReactorTransformer<>("hello_flux"));
    }

    @GetMapping("/slow")
    public Mono<String> apiDoSomethingSlow(ServerHttpResponse response) {
        return fooService.doSomethingSlow();
    }
}
