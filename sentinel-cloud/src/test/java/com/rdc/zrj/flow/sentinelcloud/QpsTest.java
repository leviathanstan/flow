package com.rdc.zrj.flow.sentinelcloud;

import com.alibaba.csp.sentinel.context.ContextUtil;
import com.rdc.zrj.flow.sentinelcloud.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author leviathanstan
 * @date 05/06/2020 19:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SentinelApplication.class)
public class QpsTest {

    @Autowired
    private UserService userController;

    @Test
    public void testQps() {
//        SentinelApplication.initFlowQpsRule();
        for (int i = 0; i < 50; i++) {
            System.out.println(userController.get(1));
        }
    }

    @Test
    public void testDegrade() throws Exception{
//        SentinelApplication.initDegradeRule();
        for (int i = 0; i < 20; i++) {
            if (i == 15) {
                //等待失败窗口过去
                Thread.sleep(3000);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(userController.getByDegrade());
                }
            }).start();
            Thread.sleep(10);
        }
        Thread.sleep(3000);
    }

    @Test
    public void testRule() {
//        SentinelApplication.initRule();
        ContextUtil.enter("ruleSource", "appC");
        System.out.println(userController.getBySource());
    }

    @Test
    public void testHotParam() throws Exception{
//        SentinelApplication.initParamFlowRules();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(userController.getParam(2));
            }).start();
        }
        Thread.sleep(2000);
    }

}
