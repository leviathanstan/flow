package com.rdc.zrj.flow.hystrixcloud.hystrix;

import com.rdc.zrj.flow.hystrixcloud.util.UserContextHolder;

import java.util.concurrent.Callable;

/**
 * @author leviathanstan
 * @date 04/29/2020 17:51
 */
public class DelegatingUserContextCallable<V> implements Callable<V> {
    private final Callable<V> delegate;
    private String originalUserContext;

    public DelegatingUserContextCallable(Callable<V> delegate,
                                         String userContext) {
        this.delegate = delegate;
        this.originalUserContext = userContext;
    }

    @Override
    public V call() throws Exception {
        UserContextHolder.userContext.set(originalUserContext);

        try {
            return delegate.call();
        }
        finally {
            this.originalUserContext = null;
        }
    }
}