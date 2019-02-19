package com.tuyu.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单
 *
 * @author tuyu
 * @date 2/18/19
 * Talk is cheap, show me the code.
 */
@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 8342153073161845518L;

    /** 订单uuid */
    private String orderUuid;

    /** 商品名称 */
    private String name;

    /** 金额，单位分，没有小数 */
    private Integer price;

    /** 下单时间 */
    private Date orderTime;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;

    /** 订单是否超时 */
    private boolean timeout;

    /** 订单是否支付 */
    private boolean payed;

    /** 订单支付时间 */
    private Date payTime;
}
