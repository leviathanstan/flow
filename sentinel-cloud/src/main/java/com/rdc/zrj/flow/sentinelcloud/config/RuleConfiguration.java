package com.rdc.zrj.flow.sentinelcloud.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.rdc.zrj.flow.sentinelcloud.source.FileDataSource;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author leviathanstan
 * @date 05/07/2020 18:56
 */
@Configuration
public class RuleConfiguration {

    public RuleConfiguration() {
        //硬编码方式
        initRule();
        //数据源配置，或使用spi方式
//        initDataSource();
    }

    public void initDataSource() {
        try {
            new FileDataSource().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initRule() {
        initFlowQpsRule();
        initParamFlowRules();
        initAcl();
        initDegradeRule();
    }

    /**
     * 流量控制
     */
    public static void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule("sayHello");
        // set flow qps to 20
        rule.setCount(2);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
    /**
     * 熔断降级
     */
    public static void initDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("sayHello");
        // set threshold rt, 10 ms
        rule.setCount(10);
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        rule.setTimeWindow(3);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }

    /**
     * 黑白名单
     */
    public static void initAcl() {
        AuthorityRule rule = new AuthorityRule();
        rule.setResource("ruleSource");
        rule.setStrategy(RuleConstant.AUTHORITY_WHITE);
        rule.setLimitApp("appA,appB");
        AuthorityRuleManager.loadRules(Collections.singletonList(rule));
    }

    /**
     * 热点参数
     */
    public static void initParamFlowRules() {
        //5qps，第0个参数
        ParamFlowRule rule = new ParamFlowRule("paramSource")
                .setParamIdx(0)
                .setGrade(RuleConstant.FLOW_GRADE_QPS)
                //.setDurationInSec(3)
                //.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_flowER)
                //.setMaxQueueingTimeMs(600)
                .setCount(15);

        //设置值为2时的Qps，会覆盖上面的通用配置（当不是2时使用的还是通用配置）
        ParamFlowItem item = new ParamFlowItem().setObject(String.valueOf(2))
                .setClassType(int.class.getName())
                .setCount(5);
        rule.setParamFlowItemList(Collections.singletonList(item));
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }
}
