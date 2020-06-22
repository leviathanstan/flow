package com.rdc.zrj.flow.hystrixcommand;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leviathanstan
 * @date 05/03/2020 20:55
 */
public class ServiceCollapseCommand extends HystrixCollapser<List<String>, String, Integer> {
    private ServiceProvider provider;
    private Integer id;

    public ServiceCollapseCommand(ServiceProvider provider, Integer id) {
        this.provider = provider;
        this.id = id;
    }

    @Override
    public Integer getRequestArgument() {
        return id;
    }

    @Override
    protected HystrixCommand<List<String>> createCommand(Collection<CollapsedRequest<String, Integer>> collapsedRequests) {
        List<Integer> ids = new ArrayList<>(collapsedRequests.size());
        ids.addAll(collapsedRequests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
        BatchCommand batchCommand = new BatchCommand(ids, provider);
        return batchCommand;
    }

    @Override
    protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Integer>> collapsedRequests) {
        int i = 0;
        for (CollapsedRequest<String, Integer> collapsedRequest : collapsedRequests) {
            String res = batchResponse.get(i++);
            collapsedRequest.setResponse(res);
        }
    }
}
