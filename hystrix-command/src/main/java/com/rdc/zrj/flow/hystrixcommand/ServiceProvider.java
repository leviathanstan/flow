package com.rdc.zrj.flow.hystrixcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author leviathanstan
 * @date 04/29/2020 21:12
 */
public class ServiceProvider {

    public String doRun() {
        Random random = new Random();
        int i = random.nextInt(20);
        if (i < 20) {
            try {
                Thread.sleep(6000);
            } catch (Exception e) {
                //ignore
                e.printStackTrace();
            }
        }
        return "success";
    }


    public List<String> batch(List<Integer> ids) {
        System.out.println(Thread.currentThread().getName());
        List<String> res = new ArrayList<>(ids.size());
        for (int i : ids) {
            res.add("id:" + String.valueOf(i));
        }
        return res;
    }
}
