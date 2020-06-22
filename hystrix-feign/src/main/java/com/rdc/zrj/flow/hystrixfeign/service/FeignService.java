package com.rdc.zrj.flow.hystrixfeign.service;

import com.rdc.zrj.flow.hystrixfeign.client.AidClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author leviathanstan
 * @date 05/06/2020 21:59
 */
@Service
public class FeignService {

    @Autowired
    private AidClient aidClient;

    public String get() {

        return aidClient.get();
    }


    private void randomSleep() {
        Random random = new Random();
        int res = random.nextInt(4);
        try {
            //1/3的概率陷入超时
            if (res <= 3)   Thread.sleep(3000);
            System.out.println("end...");
        } catch (InterruptedException e) {
            //ignore
            e.printStackTrace();
        }
    }
}
