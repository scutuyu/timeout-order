package com.tuyu.controller;

import com.tuyu.common.CircularQueue;
import com.tuyu.entity.Order;
import com.tuyu.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tuyu
 * @date 2/19/19
 * Talk is cheap, show me the code.
 */
@Slf4j
@RestController
public class OrderController {

    @Autowired
    private CircularQueue circularQueue;

    /**
     * 默认10秒
     */
    private static final int DELAY = 10;

    /**
     * 模拟添加一个订单，可以指定多少秒超时，如果不指定，默认10秒就超时
     * <pre>
     *     客户端请求：
     *          postman eg:
     *              localhost:8081/order
     *              POST
     *              name:香烟
     *              price:1000
     *              delay:5
     *          curl eg:
     *              curl -X POST --header 'Content-Type: application/x-www-form-urlencoded' --header 'Accept: text/xml' \
     *              -d 'name=香烟&price=1000&delay=8' 'http://localhost:8081/order'
     *
     * </pre>
     *
     * @param order 订单
     * @param delay 超时时间
     *
     * @return
     */
    @PostMapping("/order")
    public Object add(Order order, Integer delay) {
        if (order != null) {
            order.setOrderUuid(StringUtils.uuid());
            circularQueue.submitOrder(order, delay == null ? DELAY : delay);
            return "订单提交成功";
        }
        return "参数错误";
    }
}
