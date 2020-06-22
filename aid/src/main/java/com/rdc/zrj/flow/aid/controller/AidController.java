package com.rdc.zrj.flow.aid.controller;

import com.rdc.zrj.flow.aid.service.AidService;
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
@RequestMapping(value = "/v1")
public class AidController {

    @Autowired
    private AidService fooService;

    @GetMapping("/get")
    public Mono<String> apiNormalSingle() {
        return fooService.emitSingle();
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
