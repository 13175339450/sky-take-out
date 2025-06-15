package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 定义对订单的 待付款的自动任务处理
     * 每分钟处理一次
     * cron = "0 * * * * ?" 每分钟的0秒执行
     */
    @Scheduled(cron = "0 * * * * ?")
    public void OrderTimeOutTask(){
        //对订单未付款超时15分钟的订单 进行修改相关的状态信息
        log.info("对超时未付款订单进行处理...");

        //1.获取当前时间的前15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        //2.自定义更新方法：对所有未付款状态的订单 并且超时的订单 进行相关信息的修改 批量
        orderMapper.dealNoPayOrderBatch(Orders.PENDING_PAYMENT, time);
    }

    /**
     * 在每天的凌晨1点 对前一天的在 配送中的订单进行处理为已完成
     * 每天处理一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void OrderDeliveryTask(){
        log.info("对配送中的订单进行处理...");

        //1.获取前一天的 年月日
        LocalDateTime afterTime = LocalDateTime.now().plusHours(-1);
        LocalDateTime beforeTime = LocalDateTime.now().plusHours(-25);

        //2.批量更新 昨天的订单
        orderMapper.dealDeliveryOrderBatch(Orders.DELIVERY_IN_PROGRESS, beforeTime, afterTime);

    }
}
