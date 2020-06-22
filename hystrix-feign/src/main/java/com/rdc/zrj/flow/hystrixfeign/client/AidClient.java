package com.rdc.zrj.flow.hystrixfeign.client;

import com.rdc.zrj.flow.hystrixfeign.handle.FallbackHandle;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author leviathanstan
 * @date 05/06/2020 21:55
 */
@FeignClient(name = "aid", path = "/v1", fallbackFactory = FallbackHandle.class)
public interface AidClient {

//    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GetMapping(value = "/get")
//            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
//            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String get();
}