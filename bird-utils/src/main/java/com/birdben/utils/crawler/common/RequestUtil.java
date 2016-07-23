package com.birdben.utils.crawler.common;

import org.apache.http.Header;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;

public class RequestUtil {

    /**
     * 获取request的config
     */
    private static RequestConfig getLocalConfig() {
        RequestConfig localConfig = RequestConfig.copy(HttpClientUtil.getClientConfig())
                .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
        return localConfig;
    }

    /**
     * 获取固定的请求header
     */
    private static Header[] getRequestHeader(String host, String referer) {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("x-requestted-with", "XMLHttpRequest"));
        headers.add(new BasicHeader("Accept-Language",
                "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        headers.add(new BasicHeader("ContentType",
                "application/x-www-form-urlencoded; chartset=gbk"));
        headers.add(new BasicHeader("Host", host));
        headers.add(new BasicHeader("DNT", "1"));
        headers.add(new BasicHeader("Cache-Control", "no-cache"));
        headers.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0"));
        headers.add(new BasicHeader("Referer", referer));
        headers.add(new BasicHeader("Connection", "Keep-Alive"));
        return headers.toArray(new Header[0]);
    }

    public static HttpGet createGETRequest(String pageUrl, String headerUrl, String host) {
        HttpGet get = new HttpGet(pageUrl);
        get.setConfig(getLocalConfig());
        get.setHeaders(getRequestHeader(host, headerUrl));
        return get;
    }

    public static HttpPost createPOSTRequest(String pageUrl, String headerUrl, String host) {
        HttpPost post = new HttpPost(pageUrl);
        post.setConfig(getLocalConfig());
        post.setHeaders(getRequestHeader(host, headerUrl));
        return post;
    }
}
