package com.rdc.zrj.flow.hystrixfeign.handle;

import com.rdc.zrj.flow.hystrixfeign.client.AidClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author leviathanstan
 * @date 05/07/2020 10:54
 */
@Component
public class FallbackHandle implements FallbackFactory<AidClient> {

    @Override
    public AidClient create(Throwable throwable) {
        return new AidClient() {
            @Override
            public String get() {
                return "error !!!!";
            }
        };
    }
}
