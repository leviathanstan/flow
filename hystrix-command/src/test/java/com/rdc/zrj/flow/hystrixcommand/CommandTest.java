package com.rdc.zrj.flow.hystrixcommand;

import org.junit.Test;

import java.util.concurrent.Future;

/**
 * @author leviathanstan
 * @date 04/29/2020 21:20
 */
public class CommandTest {

    private ServiceProvider serviceProvider = new ServiceProvider();

    @Test
    public void testCommand() {
        for (int i = 0; i < 25; i++) {
            MyHystrixCommand hystrixCommand = new MyHystrixCommand(serviceProvider);
            try {
                String res = hystrixCommand.execute();
                System.out.println(res);
            } catch (Exception e) {
                System.out.println("error:{" + i + "}" + e.getMessage());
            }
        }
    }

    @Test
    public void testInterrupt() throws Exception{
        MyHystrixCommand hystrixCommand = new MyHystrixCommand(serviceProvider);
        Future<String> future = hystrixCommand.queue();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    future.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println(future.get());
        Thread.sleep(10000);
    }
}
