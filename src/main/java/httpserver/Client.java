package httpserver;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author maobing.dmb
 * @date 2017/11/10
 */
public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);

        ExecutorService executorService = Executors.newFixedThreadPool(10000);

        final AtomicInteger n = new AtomicInteger(0);
        for (int i = 0; i < 2000; i++) {
            Callable<Boolean> callable = new Callable() {
                public Boolean call() throws Exception {
                    try {
                        CloseableHttpClient client = HttpClients.createDefault();
                        HttpGet request = new HttpGet("http://127.0.0.1:8080/index");
                        RequestConfig requestConfig = RequestConfig.custom()
                            .setSocketTimeout(500).setConnectTimeout(1000).build();
                        request.setConfig(requestConfig);
                        request.setHeader("Cookie", "{}");
                        latch.await();
                        CloseableHttpResponse response = client.execute(request);
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            HttpEntity entity = response.getEntity();
                            //String strResult = EntityUtils.toString(entity, "utf-8");
                            System.out.println(entity);
                            n.incrementAndGet();
                        } else {
                            System.out.println("fail.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("exception.");
                    }
                    return true;
                }
            };
            executorService.submit(callable);
        }
        latch.countDown();
        Thread.sleep(20000);
        System.out.println(n.intValue());
    }

}
