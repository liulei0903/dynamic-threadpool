package threadpool;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description
 * @Author liulei
 * @Date 2021/6/7 16:51
 **/
public class GuavaListeningExecutorServiceTest {

    /**
     * 线程池
     */
    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 10, 60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Test
    public void test() throws Exception {
        parse();
    }

    public static String parse() throws Exception {
        List<String> result = new ArrayList<>();
        List<String> list = new ArrayList<>();

        //模拟原始数据
        for (int i = 0; i < 1211; i++) {
            list.add(i + "-");
            System.out.println("添加原始数据:" + i);
        }

        int size = 50;//切分粒度，每size条数据，切分一块，交由一条线程处理
        int countNum = 0;//当前处理到的位置
        int count = list.size() / size;//切分块数
        int threadNum = 0;//使用线程数
        if (count * size != list.size()) {
            count++;
        }

        final CountDownLatch countDownLatch = new CountDownLatch(count);

        //使用Guava的ListeningExecutorService装饰线程池
        //这里装饰的作用是可以返回 ListenableFuture ， 通过 ListenableFuture 可以add
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(threadPoolExecutor);

        while (countNum < count * size) {
            //切割不同的数据块，分段处理
            threadNum++;
            countNum += size;
            MyCallable myCallable = new MyCallable();
            myCallable.setList(ImmutableList.copyOf(
                    list.subList(countNum - size, list.size() > countNum ? countNum : list.size())));

            ListenableFuture listenableFuture = executorService.submit(myCallable);

            //方法1：
            //PS: FutureCallback 是在哪个线程中执行的？ Futures.addCallback 使用的 SameThreadExecutorService（在当前线程中执行）
            Futures.addCallback(listenableFuture, new FutureCallback<List<String>>() {
                //任务处理成功时执行
                @Override
                public void onSuccess(List<String> list) {
                    countDownLatch.countDown();
                    System.out.println("第h次处理完成");
                    result.addAll(list);
                }

                //任务处理失败时执行
                @Override
                public void onFailure(Throwable throwable) {
                    countDownLatch.countDown();
                    System.out.println("处理失败：" + throwable);
                }
            });

            //方法2：
            listenableFuture.addListener(new Runnable() {
                @Override
                public void run() {

                }
            } , MoreExecutors.sameThreadExecutor());

        }

        //设置时间，超时了直接向下执行，不再阻塞
        countDownLatch.await(3, TimeUnit.SECONDS);

        result.stream().forEach(s -> System.out.println(s));
        System.out.println("------------结果处理完毕，返回完毕,使用线程数量：" + threadNum);

        return "处理完了";
    }

    static class MyCallable implements Callable {

        private List<String> list;

        @Override
        public Object call() throws Exception {
            List<String> listReturn = new ArrayList<>();
            //模拟对数据处理，然后返回
            for (int i = 0; i < list.size(); i++) {
                listReturn.add(list.get(i) + "：处理时间：" + System.currentTimeMillis() + "---:处理线程：" + Thread.currentThread());
            }

            return listReturn;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}
