package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "营业额统计相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistic(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("开始时间：{}, 结束时间：{}", begin, end);

        TurnoverReportVO vo = reportService.turnoverStatistic(begin, end);
        return Result.success(vo);
    }

    /**
     * 用户统计接口
     */
    @GetMapping("userStatistics")
    @ApiOperation("用户统计接口")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("开始时间：{}, 结束时间：{}", begin, end);
        UserReportVO vo = reportService.userStatistics(begin, end);
        return Result.success(vo);
    }

    //订单统计
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end){
        log.info("订单数据统计：{},{}",begin,end);
        return Result.success(reportService.getOrderStatistics(begin,end));
    }

    //销量排名top10
    @GetMapping("/top10")
    @ApiOperation("销量排名top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end){
        log.info("销量排名top10：{},{}",begin,end);
        return Result.success(reportService.getSalesTop10(begin,end));
    }

    /**
     * 导出Excel表功能
     */
    @GetMapping("export")
    @ApiOperation("导出excel表")
    public void export(HttpServletResponse response){
        reportService.export(response);
    }
}
