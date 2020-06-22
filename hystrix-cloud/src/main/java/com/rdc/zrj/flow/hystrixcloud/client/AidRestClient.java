package com.rdc.zrj.flow.hystrixcloud.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author leviathanstan
 * @date 05/06/2020 18:47
 */
@Component
public class AidRestClient {

    @Autowired
    RestTemplate restTemplate;

    public String getResponse(Integer id) {
        ResponseEntity<String> restExchange =
                restTemplate.exchange(
                        "http://localhost:8081/v1/get",
                        HttpMethod.GET,
                        null, String.class, id);

        return restExchange.getBody();
    }
}
