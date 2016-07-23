package com.birdben.utils.crawler.app;

import akka.actor.UntypedActor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.birdben.utils.crawler.common.HttpClientUtil;
import com.birdben.utils.crawler.common.RequestUtil;
import com.birdben.utils.crawler.common.ResponseProcessor;
import org.apache.http.client.methods.HttpGet;

import java.util.HashMap;
import java.util.Map;

public class WanshenmeWorker extends UntypedActor {

    @Override
    public void onReceive(Object obj) {
        try {
            // 抓取url对应的html
            String url = obj.toString();
            System.out.println("正在获取活动详情：" + url);
            HttpGet request = RequestUtil.createGETRequest(url, "http://wanapi.damai.cn", "wanapi.damai.cn");
            String jsonString = new ResponseProcessor(HttpClientUtil.getClient().execute(request)).getContent();

            String baseShareUrl = "http://m.jtwsm.cn/proj/{{id}}.html";

            String sourceUrl = "";
            String title = "";
            String time = "";
            String nums = "";
            String location = "";
            String detail = "";
            String price = "";
            String organizers = "";
            String phone = "";
            String tag = "";
            String site = WanshenmeShare.site;

            JSONObject root = JSONObject.parseObject(jsonString);
            String code = root.getString("errorCode");
            if ("0".equals(code)) {
                JSONObject data = root.getJSONObject("data");
                JSONObject info = data.getJSONObject("projinfo");

                // 活动名称
                title = WanshenmeShare.HandleMessyCode(info.getString("title"));

                // 时间
                time = info.getString("timestr");

                // 地点
                String cityname = info.getString("cityname");
                if ("北京市".equals(cityname) || "杭州市".equals(cityname)) {
                    location = info.getString("address");
                }

                // 标签分类
                tag = info.getString("categoryname");

                // 价格
                price = info.getString("pricestr");

                // 用户名
                organizers = info.getString("orgname");

                // 详细描述
                detail = WanshenmeShare.HandleMessyCode(data.getString("text"));

                // 详情页面url
                sourceUrl = baseShareUrl.replace("{{id}}", info.getString("_id"));
            }

            Map<String, String> bean = new HashMap<String, String>();
            bean.put("surl", sourceUrl);
            bean.put("title", title);
            bean.put("time", time);
            bean.put("nums", nums);
            bean.put("location", location);
            bean.put("detail", detail);
            bean.put("site", site);
            bean.put("price", price);
            bean.put("organizers", organizers);
            bean.put("phone", phone);
            bean.put("tag", tag);

            // 这里可以保存bean到数据库中
            System.out.println("已获取活动详情:" + JSON.toJSONString(bean));
        } catch (Exception e) {
            WanshenmeShare.add(obj.toString());
            e.printStackTrace();
        }
        //计数器-1
        WanshenmeShare.count.decrementAndGet();
    }
}
