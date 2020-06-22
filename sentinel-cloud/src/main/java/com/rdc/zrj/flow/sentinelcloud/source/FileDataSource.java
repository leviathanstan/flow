package com.rdc.zrj.flow.sentinelcloud.source;

import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.FileWritableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.net.URLDecoder;
import java.util.List;

/**
 * Pull模式
 * @author leviathanstan
 * @date 05/07/2020 18:31
 */
public class FileDataSource implements InitFunc {
    @Override
    public void init() throws Exception {
        String flowRulePath = URLDecoder.decode(getClass().getClassLoader().getResource("FlowRule.json").getFile(), "UTF-8");;

        ReadableDataSource<String, List<FlowRule>> ds = new FileRefreshableDataSource<>(
                flowRulePath, source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {})
        );
        // 将可读数据源注册至 FlowRuleManager.
        FlowRuleManager.register2Property(ds.getProperty());

        WritableDataSource<List<FlowRule>> wds = new FileWritableDataSource<>(flowRulePath, this::encodeJson);
        // 将可写数据源注册至 transport 模块的 WritableDataSourceRegistry 中.
        // 这样收到控制台推送的规则时，Sentinel 会先更新到内存，然后将规则写入到文件中.
        WritableDataSourceRegistry.registerFlowDataSource(wds);
        //degrade可写
//        WritableDataSource<List<DegradeRule>> des = new FileWritableDataSource<List<DegradeRule>>(degradeRulePath, this::encodeJson);
//        WritableDataSourceRegistry.registerDegradeDataSource(des);
    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }
}