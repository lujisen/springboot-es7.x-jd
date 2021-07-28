package com.ljs.hadoop.springbootes.utils;

import com.ljs.hadoop.springbootes.pojo.Content;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * @author: Created By lujisen
 * @company China JiNan
 * @date: 2021-07-27 13:39
 * @version: v1.0
 * @description: com.ljs.hadoop.springbootes.utils
 */
@Component
public class HtmlParseUtil {

    /*public static void main(String[] args) throws Exception {

        String url = "https://search.jd.com/Search?keyword=" + "java"+"&enc=utf-8";

        String url = "https://search.jd.com/Search?keyword=java";
        //解析网页 返回的是JS document页面
        Document document = Jsoup.parse(new URL(url),3000);
        Document document = Jsoup.parse(new URL(new String(url.getBytes(),"utf-8")), 30000);

        //打印DOM对象的html
        System.out.println(document.html());
        //打印DOM对象的所有text
        System.out.println(document.text());
        System.out.println(document.toString());s
        //JS里面使用的所有方法这里都能使用
        Element element = document.getElementById("J_goodsList");
        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        //会将需要的网页数据保存到数组中
        ArrayList<Content> goodsList = new ArrayList<>();

        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setTitle(title);
            content.setImg(img);
            content.setPrice(price);
            goodsList.add(content);
        }
        System.out.println(goodsList);
    }*/

    private PoolingHttpClientConnectionManager cm;

    public HtmlParseUtil() {
        this.cm = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        cm.setMaxTotal(200);
        //设置每个主机的并发数
        cm.setDefaultMaxPerRoute(20);
    }
    /**
     * 根据请求地址下载页面数据
     *
     * @param keyword
     * @return 页面数据
     */
    public List<Content> parseJD(String keyword) {

        String url = "https://search.jd.com/Search?keyword=" + keyword+"&enc=utf-8";
        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();
        //声明httpGet请求对象
        HttpGet httpGet = new HttpGet(url);
        //设置请求参数RequestConfig
        httpGet.setConfig(this.getConfig());
        //设置请求Request Headers中的User-Agent，告诉京东说这是浏览器访问
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Mobile Safari/537.36");
        CloseableHttpResponse response=null;
        try {
            //使用HttpClient发起请求，返回response
            response = httpClient.execute(httpGet);
            //解析response返回数据
            if (response.getStatusLine().getStatusCode() == 200) {
                String html = "";
                //如果response.getEntity获取的结果为空，在执行EntityUtils.toString会报错
                //需要对Entity进行非空的判断
                if (response.getEntity() != null) {
                    html = EntityUtils.toString(response.getEntity(), "utf8");
                    //解析html获取Document
                    Document document = Jsoup.parse(html);
                    Element element = document.getElementById("J_goodsList");
                    //获取所有的li元素
                    Elements elements = element.getElementsByTag("li");
                    //会将需要的网页数据保存到数组中
                    ArrayList<Content> goodsList = new ArrayList<>();

                    for (Element el : elements) {
                        String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
                        String price = el.getElementsByClass("p-price").eq(0).text();
                        String title = el.getElementsByClass("p-name").eq(0).text();
                        String shop = el.getElementsByClass("p-shopnum").eq(0).text();
                        Content content = new Content();
                        content.setTitle(title);
                        content.setImg(img);
                        content.setPrice(price);
                        content.setShop(shop);

                        goodsList.add(content);
                    }
                    return goodsList;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                // 不能关闭，现在使用的是连接管理器
                // httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //下载失败，返回空串
        return null;
    }
    //获取请求参数对象
    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1000)  //创建连接的最长时间
                .setConnectionRequestTimeout(500) //获取连接的最长时间
                .setSocketTimeout(100000)  //数据传输的最长时间
                .build();
        return config;
    }
}
