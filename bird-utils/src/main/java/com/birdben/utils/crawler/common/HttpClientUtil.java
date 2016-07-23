package com.birdben.utils.crawler.common;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;

public class HttpClientUtil {

    /**
     * 获取client配置
     */
    public static RequestConfig getClientConfig() {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec("easy").setSocketTimeout(10000)
                .setConnectTimeout(10000).build();

        return globalConfig;
    }

    /**
     * cookie访问策略注册
     */
    private static Registry<CookieSpecProvider> getRegistry() {

        CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
            public CookieSpec create(HttpContext context) {

                return new BrowserCompatSpec() {
                    @Override
                    public void validate(Cookie cookie, CookieOrigin origin)
                            throws MalformedCookieException {
                        return;
                    }
                };
            }

        };
        Registry<CookieSpecProvider> registry = RegistryBuilder
                .<CookieSpecProvider>create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory())
                .register("easy", easySpecProvider).build();

        return registry;
    }

    /**
     * 获取httpclient,由于需要保持http的连接状态， 所以一个客户请求需要一直使用一个HTTPclient
     */
    public static CloseableHttpClient getClient() {
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient client = HttpClients.custom()
                .setDefaultCookieSpecRegistry(getRegistry())
                .setDefaultRequestConfig(getClientConfig())
                .setDefaultCookieStore(cookieStore).build();
        return client;
    }
}
