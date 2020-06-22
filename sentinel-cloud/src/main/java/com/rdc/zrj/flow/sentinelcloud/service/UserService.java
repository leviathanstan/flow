package com.rdc.zrj.flow.sentinelcloud.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.stereotype.Service;

/**
 * @author leviathanstan
 * @date 05/06/2020 17:59
 */
@Service
public class UserService {

    @SentinelResource(value = "sayHello", blockHandler = "exceptionHandler", fallback = "helloFallback")
    public String get(long s) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }

    // Fallback 函数，函数签名与原函数一致或加一个 Throwable 类型的参数.
    public String helloFallback(long s) {
        return String.format("Halooooo %d", s);
    }

    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public String exceptionHandler(long s, BlockException ex) {
        // Do some log here.
        ex.printStackTrace();
        return "Oops, error occurred at " + s;
    }

    @SentinelResource(value = "sayHello")
    public String getByDegrade() {
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @SentinelResource(value = "ruleSource")
    public String getBySource() {

        return "source";
    }

    @SentinelResource(value = "paramSource")
    public String getParam(int id) {

        return "success: " + id;
    }
}
