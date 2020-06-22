package com.rdc.zrj.flow.hystrixgateway;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//import org/springframework/cloud/gateway/filter/factory/HystrixGatewayFilterFactory
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.containsEncodedParts;

/**
 * @author leviathanstan
 * @date 05/07/2020 14:28
 */
@Component
public class CustomHystrixFilter extends AbstractGatewayFilterFactory<CustomHystrixFilter.Config> {

    private static final String FORWARD_KEY = "forward";
    private static final String NAME = "CustomHystrix";
    private static final int TIMEOUT_MS = 1000;
    private final ObjectProvider<DispatcherHandler> dispatcherHandlerProvider;
    private volatile DispatcherHandler dispatcherHandler;
    private boolean processConfig = false;

    public CustomHystrixFilter(ObjectProvider<DispatcherHandler> dispatcherHandlerProvider) {
        super(Config.class);
        this.dispatcherHandlerProvider = dispatcherHandlerProvider;
    }

    private DispatcherHandler getDispatcherHandler() {
        if (dispatcherHandler == null) {
            dispatcherHandler = dispatcherHandlerProvider.getIfAvailable();
        }

        return dispatcherHandler;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(NAME_KEY);
    }


    @Override
    public GatewayFilter apply(Config config) {
        processConfig(config);
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().pathWithinApplication().value();
            int timeout = config.getTimeout().getOrDefault(path, TIMEOUT_MS);
            CustomHystrixCommand command = new CustomHystrixCommand(config.getFallbackUri(), exchange, chain, timeout, path);
            return Mono.create(s -> {
                Subscription sub = command.toObservable().subscribe(s::success, s::error, s::success);
                s.onCancel(sub::unsubscribe);
            }).onErrorResume((Function<Throwable, Mono<Void>>) throwable -> {
                if (throwable instanceof HystrixRuntimeException) {
                    HystrixRuntimeException e = (HystrixRuntimeException) throwable;
                    HystrixRuntimeException.FailureType failureType = e.getFailureType();
                    switch (failureType) {
                        case TIMEOUT:
                            return Mono.error(new TimeoutException());
                        case COMMAND_EXCEPTION: {
                            Throwable cause = e.getCause();
                            if (cause instanceof ResponseStatusException || AnnotatedElementUtils
                                    .findMergedAnnotation(cause.getClass(), ResponseStatus.class) != null) {
                                return Mono.error(cause);
                            }
                        }
                        default:
                            break;
                    }
                }
                return Mono.error(throwable);
            }).then();
        };
    }

    /**
     * YAML解析的时候MAP的KEY不支持'/'，这里只能用'-'替代
     *
     * @param config config
     */
    private void processConfig(Config config) {
        if (!processConfig) {
            processConfig = true;
            if (null != config.getTimeout()) {
                Map<String, Integer> timeout = new HashMap<>(8);
                config.getTimeout().forEach((k, v) -> {
                    String key = k.replace("-", "/");
                    if (!key.startsWith("/")) {
                        key = "/" + key;
                    }
                    timeout.put(key, v);
                });
                config.setTimeout(timeout);
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }

    private class CustomHystrixCommand extends HystrixObservableCommand<Void> {

        private final URI fallbackUri;
        private final ServerWebExchange exchange;
        private final GatewayFilterChain chain;

        public CustomHystrixCommand(URI fallbackUri,
                                    ServerWebExchange exchange,
                                    GatewayFilterChain chain,
                                    int timeout,
                                    String key) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(key))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(key))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(timeout)));
            this.fallbackUri = fallbackUri;
            this.exchange = exchange;
            this.chain = chain;
        }

        @Override
        protected Observable<Void> construct() {
            return RxReactiveStreams.toObservable(this.chain.filter(exchange));
        }

        @Override
        protected Observable<Void> resumeWithFallback() {
            if (null == fallbackUri) {
                return super.resumeWithFallback();
            }
            URI uri = exchange.getRequest().getURI();
            boolean encoded = containsEncodedParts(uri);
            URI requestUrl = UriComponentsBuilder.fromUri(uri)
                    .host(null)
                    .port(null)
                    .uri(this.fallbackUri)
                    .build(encoded)
                    .toUri();
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
            ServerHttpRequest request = this.exchange.getRequest().mutate().uri(requestUrl).build();
            ServerWebExchange mutated = exchange.mutate().request(request).build();
            return RxReactiveStreams.toObservable(getDispatcherHandler().handle(mutated));
        }
    }

    public static class Config {

        private String id;
        private URI fallbackUri;
        /**
         * url -> timeout ms
         */
        private Map<String, Integer> timeout;

        public String getId() {
            return id;
        }

        public Config setId(String id) {
            this.id = id;
            return this;
        }

        public URI getFallbackUri() {
            return fallbackUri;
        }

        public Config setFallbackUri(URI fallbackUri) {
            if (fallbackUri != null && !FORWARD_KEY.equals(fallbackUri.getScheme())) {
                throw new IllegalArgumentException("Hystrix Filter currently only supports 'forward' URIs, found " + fallbackUri);
            }
            this.fallbackUri = fallbackUri;
            return this;
        }

        public Map<String, Integer> getTimeout() {
            return timeout;
        }

        public Config setTimeout(Map<String, Integer> timeout) {
            this.timeout = timeout;
            return this;
        }
    }
}
