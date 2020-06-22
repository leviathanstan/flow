package com.rdc.zrj.flow.aid;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.util.*;

/**
 * @author leviathanstan
 * @date 05/12/2020 11:51
 */

public class HttpTest {

    @Test
    public void sendGet() {
        int success = 0, fail = 0;
        try {
            for (int i = 0; i < 100; i++) {
                String port = "808" + getRandomPort();
                HttpGet request = new HttpGet("http://localhost:" + port + "/hello/me");
                HttpClientBuilder builder = HttpClientBuilder.create();
                HttpClient httpClient = builder.build();
                HttpResponse response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String result = EntityUtils.toString(response.getEntity(),"utf-8");
                    if (result.startsWith("Hello")) {
                        success++;
                    } else if (result.startsWith("Oops")) {
                        fail++;
                    }
                }
            }
            System.out.println(new Date() + ">>>>> success:" + success + "   fail:" + fail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendGateWay() {
        int success = 0, fail = 0;
        try {
            for (int i = 0; i < 100; i++) {
                String port = "8090";
                HttpGet request = new HttpGet("http://localhost:" + port + "/product/v1/get");
                HttpClientBuilder builder = HttpClientBuilder.create();
                HttpClient httpClient = builder.build();
                HttpResponse response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    success++;
                } else {
                    fail++;
                }
            }
            System.out.println(new Date() + ">>>>> success:" + success + "   fail:" + fail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRandomPort() {
        List<Integer> list = Arrays.asList(3, 4, 5);
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }
}
