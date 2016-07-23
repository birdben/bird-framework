package com.birdben.utils.crawler.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.birdben.utils.crawler.common.HttpClientUtil;
import com.birdben.utils.crawler.common.RequestUtil;
import com.birdben.utils.crawler.common.ResponseProcessor;
import org.apache.http.client.methods.HttpGet;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WanshenmeApp extends UntypedActor {

    private static final ActorSystem as = ActorSystem.create("Master");
    private static AtomicInteger count = new AtomicInteger(0);
    private static final ActorRef[] workers = getWorkers();
    private static final int workerNums = 100;

    public static void startCrawler() throws Exception {
        System.out.println("---------------爬取开始---------------");

        ActorRef WanshenmeApp = as.actorOf(Props.create(WanshenmeApp.class));
        List<String> urllist = getUrllist();

        // 抓取并解析所有url
        for (int i = 0; i < urllist.size(); i++) {
            // 计数器+1
            WanshenmeShare.count.incrementAndGet();
            WanshenmeApp.tell(urllist.get(i), WanshenmeApp);
            // 休眠 避免请求频率过高
            TimeUnit.MILLISECONDS.sleep(WanshenmeShare.detail_sleep_millis);
        }

        // 重新抓取‘失败的url’ 最多尝试5次
        handleExceptionUrls(WanshenmeApp);

        System.out.println("---------------爬取结束---------------");
    }

    private static List<String> getUrllist() throws Exception {

        List<String> list = new ArrayList<String>();
        Map<String, String> timeoutMap = new HashMap<String, String>();
        // 一級
        int city = 110000;//北京
        String mainurl = "http://wanapi.damai.cn/projlistnew.json?cateid=0&cityId={{city}}&etime=&order=pri&pindex={{N}}&psize=20&source=10344&stime=&version=20001";
        String detailUrl = "http://wanapi.damai.cn/proj.json?id={{id}}&source=10344&version=20001";
        mainurl = mainurl.replace("{{city}}", String.valueOf(city));
        JSONArray responseData = new JSONArray();
        int i = 0;
        while (responseData.size() != 0 || i == 0) {
            System.out.println("---------------------------------------");
            System.out.println("正在爬取第 " + i + " 页");
            String url = mainurl.replace("{{N}}", String.valueOf(i));
            System.out.println("正在爬取 URL:" + url);
            // 一級--获取html （refer host 暂时写死）
            String jsonString;
            try {
                HttpGet request = RequestUtil.createGETRequest(url, "http://wanapi.damai.cn", "wanapi.damai.cn");
                jsonString = new ResponseProcessor(HttpClientUtil.getClient().execute(request)).getContent();
            } catch (SocketTimeoutException ex) {
                //ex.printStackTrace();
                // 添加到超时队列
                timeoutMap.put(url, url);
                System.out.println("SocketTimeoutException 超时链接");
                continue;
            } catch (Exception e1) {
                e1.printStackTrace();
                continue;
            }

            System.out.println("获取爬取数据 jsonString:" + jsonString);
            JSONObject root = JSONObject.parseObject(jsonString);
            String msg = root.getString("errorCode");
            if ("0".equals(msg)) {
                JSONArray dataList = root.getJSONArray("data");
                for (int k = 0; k < dataList.size(); k++) {
                    JSONObject huodongBean = dataList.getJSONObject(k);
                    String subUrl = detailUrl.replace("{{id}}", huodongBean.getString("id"));
                    list.add(subUrl);
                }
                responseData = dataList;
            }
            i++;

            TimeUnit.MILLISECONDS.sleep(WanshenmeShare.list_sleep_millis);
        }

        // TODO : 这里可以对timeoutMap超时队列进行处理

        System.out.println("共获取到了" + list.size() + "个活动详情URL地址");
        return list;
    }

    private static void handleExceptionUrls(ActorRef WanshenmeApp) throws InterruptedException {
        int cycle = 0;
        while (true) {
            System.out.println("失败URL处理中...");
            if (WanshenmeShare.count.intValue() == 0) {
                Set<String> set = WanshenmeShare.getSet();
                WanshenmeShare.clearSet();
                Iterator<String> it = set.iterator();
                while (it.hasNext()) {
                    // 计数器+1
                    WanshenmeShare.count.incrementAndGet();
                    String url = it.next();
                    WanshenmeApp.tell(url, WanshenmeApp);
                    System.out.println("失败URL为： " + url);
                    // 休眠 避免请求频率过高
                    TimeUnit.MILLISECONDS.sleep(WanshenmeShare.detail_sleep_millis);
                }
                // 本轮完毕
                cycle++;
                System.out.println("失败URL处理中,第" + cycle + "轮完毕...");
                if (cycle == 6) {
                    break;
                }
            } else {
                TimeUnit.MILLISECONDS.sleep(WanshenmeShare.detail_sleep_millis);
            }
        }
    }

    @Override
    public void preStart() {
        count.incrementAndGet();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        ActorRef worker = WanshenmeApp.getWorker();
        worker.tell(o, worker);
    }

    public static ActorRef getWorker() {
        Random r = new Random();
        int i = r.nextInt(workerNums);
        return workers[i];
    }

    private static final ActorRef[] getWorkers() {
        ActorRef[] tmp = new ActorRef[workerNums];
        for (int i = 0; i < workerNums; i++) {
            tmp[i] = as.actorOf(Props.create(WanshenmeWorker.class), i + "");
        }
        return tmp;
    }

    @Override
    public void postStop() {
        count.decrementAndGet();
    }
}