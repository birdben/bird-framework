package com.birdben.utils.crawler.common;

import com.birdben.utils.crawler.constants.CrawlConst;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * response操作辅助类
 */
public class ResponseProcessor {

    private CloseableHttpResponse resp;

    public ResponseProcessor(CloseableHttpResponse resp) {
        this.resp = resp;
    }

    /**
     * 获取二进制流
     */
    public byte[] getData() throws Exception {
        try {
            return EntityUtils.toByteArray(resp.getEntity());
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (resp != null) {
                resp.close();
            }
        }
    }

    /**
     * 获取文本内容
     */
    public String getContent() throws Exception {
        try {
            String content = EntityUtils.toString(resp.getEntity(), CrawlConst.DEFAULT_CHARSET);
            return content;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (resp != null) {
                resp.close();
            }
        }
    }
}