package com.rdc.zrj.flow.sentinelflux;

//import WebFluxConfig;
import com.rdc.zrj.flow.sentinelflux.controller.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author leviathanstan
 * @date 05/07/2020 20:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SentinelFluxApplication.class)
public class WebFluxTest {

    @Autowired
    UserController userController;

    @Test
    public void testFlux() throws Exception{
//        WebFluxConfig.initFlowQpsRule();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                System.out.println(userController.apiNormalFlux());
            }).start();
        }
        Thread.sleep(2000);
    }
}
