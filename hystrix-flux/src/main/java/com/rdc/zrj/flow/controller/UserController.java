package com.rdc.zrj.flow.controller;

import com.rdc.zrj.flow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
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
        return HystrixCommands.from(fooService.emitSingle())
                .fallback(Mono.just("error----"))
                .commandName("single")
                .toMono();
    }

    @GetMapping("/flux")
    public Flux<Integer> apiNormalFlux() {
        return fooService.emitMultiple();
    }

    @GetMapping("/slow")
    public Mono<String> apiDoSomethingSlow(ServerHttpResponse response) {
        return fooService.doSomethingSlow();
    }
}
