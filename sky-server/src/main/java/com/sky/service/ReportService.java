package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 统计营业额
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistic(LocalDate begin, LocalDate end);

    /**
     * 统计用户
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);


    //统计指定时间区间内的订单数据
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    //统计指定时间区间内的销量排名前10
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
