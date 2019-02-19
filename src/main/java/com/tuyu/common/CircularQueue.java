package com.tuyu.common;

import com.tuyu.entity.Order;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 环形队列
 * <p>
 *     用环形队列来实现订单超时自动关闭
 * </p>
 *
 * @author tuyu
 * @date 2/18/19
 * Talk is cheap, show me the code.
 */
@Slf4j
@Component
public class CircularQueue {

    /**
     * 循环队列的默认容量
     */
    private static final int DEFAULT_SIZE = 3600;
    /**
     * 缓存环形队列中的所有订单的Map的默认容量
     */
    private static final int DEFAULT_CACHE_SIZE = 1024;

    /** 缓存订单在环形队列中的索引，key=订单uuid，value=索引 */
    private Map<String, Integer> cachedMap;

    /** 环形队列，数组实现 */
    private OrderSet[] table;

    /** 指针 */
    private AtomicInteger point = new AtomicInteger(0);


    /**
     * 构造函数中会new一个线程来轮询环形队列
     */
    public CircularQueue() {
        this.table = new OrderSet[DEFAULT_SIZE];
        this.cachedMap = new HashMap<>(DEFAULT_CACHE_SIZE);
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        scheduledExecutorService.scheduleAtFixedRate(
                new RoundRinTimerTask(this), 0L, 1L, TimeUnit.SECONDS);
    }


    /**
     * 提交一个订单到环形队列中，一定时间内没有付款，该订单就会自动关闭
     *
     * @param order 订单
     * @param delay 订单保留期限，单位: 秒
     */
    public void submitOrder(Order order, int delay) {
        int p = this.point.get() + delay;
        int idx = index(p);
        int cycleNum = cycleNumber(p);
        OrderSet set = null;
        if ((set = table[idx]) == null) {
            set = table[idx] = new OrderSet();
        }
        synchronized (this){
            log.info("submit an order: {}, timeout in {} seconds", order.getOrderUuid(), delay);
            set.add(new TimingTask(order, delay, cycleNum));
            cachedMap.put(order.getOrderUuid(), idx);
            log.info("idx: {}", idx);
            log.info("set: {}", set);
            log.info("cachedMap: {}", cachedMap);
        }
    }

    /**
     * 支付订单，将订单从环形队列中移除
     *
     * @param orderUuid 订单uuid
     *
     * @return
     */
    public boolean payOrder(String orderUuid){
        if (cachedMap.containsKey(orderUuid)) {
            Integer idx = cachedMap.get(orderUuid);
            OrderSet set = table[idx];
            if (set != null) {
                for (TimingTask task : set) {
                    if (orderUuid.equals(task.getOrderUuid())) {
                        synchronized (this) {
                            task.payment();
                            set.remove(task);
                            cachedMap.remove(orderUuid);
                            log.info("pay an order: {}");
                        }
                        break;
                    }
                }
            }
        }
        log.error("订单号不存在");
        return false;
    }

    /**
     *
     * @param idx
     *
     * @return
     */
    private int index(int idx) {
        return idx % DEFAULT_SIZE;
    }

    /**
     * 计算订单在循环队列中存在的圈数
     * @param idx 索引
     *
     * @return
     */
    private int cycleNumber(int idx) {
        return idx / DEFAULT_SIZE;
    }

    /**
     * 计算指针下一个应该指向的索引
     * @return
     */
    private int nextPoint() {
        int p = this.point.get();
        p = index(p + 1);
        return p;
    }

    /**
     * 封装订单的Wrapper
     */
    @Data
    private class TimingTask {

        /** 订单uuid */
        private String orderUuid;

        /** 订单 */
        private Order order;

        /** 延时，多少秒后执行任务 */
        private int delay;

        /** 订单 */
        private int cycleNum;

        /**
         * 包装订单，让其可以保存在循环队列的Set集合中
         *
         * @param order
         * @param delay
         * @param cycleNum
         */
        public TimingTask(Order order, int delay, int cycleNum) {
            this.order = order;
            this.orderUuid = order.getOrderUuid();
            this.delay = delay;
            this.cycleNum = cycleNum;
        }

        /**
         * 设置订单超时
         */
        public void timeout() {
            this.order.setTimeout(true);
            this.order.setUpdateTime(new Date());
        }

        /**
         * 支付订单
         */
        public void payment() {
            Date now = new Date();
            this.order.setPayed(true);
            this.order.setPayTime(now);
            this.order.setUpdateTime(now);
        }
    }

    /**
     * 放入循环队列的对象，实质是一个Set集合
     */
    private class OrderSet extends HashSet<TimingTask> {

    }

    /**
     * 轮询循环队列的任务，将当前Set集合取出交由别的线程扫描，队列指针加一
     */
    private static class RoundRinTimerTask implements Runnable {

        private CircularQueue circularQueue;
        private ExecutorService executor;

        public RoundRinTimerTask(CircularQueue circularQueue) {
            this.circularQueue = circularQueue;
            this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(10),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.AbortPolicy());
        }

        /**
         * 对循环队列进行轮询，将扫描到的OrderSet对象让线程池去处理
         */
        @Override
        public void run() {
            synchronized (circularQueue) {
                // 循环队列当前指针
                int curPoint = circularQueue.point.get();
                // 循环队列指针下一个应该指向的索引
                int nextPoint = circularQueue.nextPoint();
                log.info("cur point: {}, next point: {}", curPoint, nextPoint);
                OrderSet set = circularQueue.table[curPoint];
                executor.submit(new TimeoutTask(circularQueue, set));
                circularQueue.point.set(nextPoint);
            }
        }
    }

    /**
     * 扫描Set集合的任务
     */
    private static class TimeoutTask implements Runnable {

        private CircularQueue circularQueue;
        private OrderSet set;

        public TimeoutTask(CircularQueue circularQueue, OrderSet set) {
            this.circularQueue = circularQueue;
            this.set = set;
        }

        /**
         * 遍历环形队列中的Set集合，移除超时的订单，为超时的订单cycleNum减一
         */
        @Override
        public void run() {
            if (set == null) {
                return;
            }
            Iterator<TimingTask> iterator = set.iterator();
            for (; iterator.hasNext();) {
                TimingTask task = iterator.next();
                int cycleNum = task.getCycleNum();
                if (cycleNum == 0) {
                    synchronized (circularQueue) {
                        iterator.remove();
                        task.timeout();
                        circularQueue.cachedMap.remove(task.getOrderUuid());
                        log.info("timeout task uuid: {}", task.getOrderUuid());
                        log.info("order timeout: {}", task.getOrder());
                        log.info("set: {}", set);
                        log.info("cachedMap: {}", circularQueue.cachedMap);
                    }
                } else {
                    task.setCycleNum(cycleNum - 1);
                }
            }
        }
    }
}
