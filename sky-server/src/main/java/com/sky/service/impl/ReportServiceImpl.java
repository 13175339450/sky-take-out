package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistic(LocalDate begin, LocalDate end) {
        //存放日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        //获取所有开始到结束的所有天数
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存每天对应的营业额
        List<Double> trunoverList = new ArrayList<>();

        for (LocalDate day : dateList) {
            //查询日期对应的营业额 但是LocalDate没有时分秒
            LocalDateTime beginTime = LocalDateTime.of(day, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(day, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);//必须是已完成的订单

            Double amount = reportMapper.turnoverStatistic(map);
            amount = amount == null ? 0.0 : amount;
            trunoverList.add(amount);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ','))
                .turnoverList(StringUtils.join(trunoverList, ','))
                .build();
    }

    /**
     * 统计用户信息
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //存储时间
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存储总用户
        List<Integer> userTotal = new ArrayList<>();
        List<Integer> newUserNumber = new ArrayList<>();

        for (LocalDate date : dateList) {
            //转换为每天的 开始和结束
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //封装倒map里
            Map map = new HashMap();
            map.put("end", endTime);

            //进行查询 并且将结果存储
            Integer total = userMapper.getUserNumberByTime(map);
            userTotal.add(total == null ? 0 : total);

            map.put("begin", beginTime);
            Integer newTotal = userMapper.getUserNumberByTime(map);
            newUserNumber.add(newTotal == null ? 0 : newTotal);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ','))
                .totalUserList(StringUtils.join(userTotal, ','))
                .newUserList(StringUtils.join(newUserNumber, ','))
                .build();
    }

    //统计指定时间区间内的订单数据
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的订单总数
        List<Integer> orderCountList = new ArrayList<>();
        //存放每天的有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();
        //便利dateList集合，查询每天的有效订单数和订单总数
        for(LocalDate date : dateList){
            //查询每天的订单总数 select count(id) from orders where order_time > ? and order_time < ?
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            //查询每天的有效订单数select count(id) from orders where order_time > ? and order_time < ? and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //计算时间区间内的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            //计算订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    //统计指定时间区间内的销量排名前10
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");
        return SalesTop10ReportVO.builder().nameList(nameList).numberList(numberList).build();
    }

    /**
     * 导出excel表功能
     */
    @Override
    public void export(HttpServletResponse response) {
        //获取30天前
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        //查询最近30天的概览数据
        BusinessDataVO businessData
                = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //执行写操作
        //1.先获取输入流 利用反射 获取当前类路径
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("./template/运营数据报表模板.xlsx");
        try {
            //读取本地的excel表
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取excel的sheet表格 通过表名获取
            XSSFSheet sheet1 = excel.getSheet("sheet1");

            //获取sheet1的第二行
            XSSFRow row = sheet1.getRow(1);
            //获取row行的 第二列 并且赋值
            row.getCell(1).setCellValue("时间: " + begin + " 至 " + end);

            //对概览数据赋值
            row = sheet1.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row = sheet1.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                //获取每一天的日期
                LocalDate newDate = begin.plusDays(i);
                //获取该天的0点和23点
                LocalDateTime beginTime = LocalDateTime.of(newDate, LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(newDate, LocalTime.MAX);
                BusinessDataVO data = workspaceService.getBusinessData(beginTime, endTime);
                //插入数据
                row = sheet1.getRow(7 + i);
                row.getCell(1).setCellValue(newDate.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());
            }

            //通过response获取输出流
            ServletOutputStream out = response.getOutputStream();
            //执行写操作
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //根据条件统计订单数量
    private Integer getOrderCount(LocalDateTime begin,LocalDateTime end,Integer status){
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.countByMap(map);
    }

}
