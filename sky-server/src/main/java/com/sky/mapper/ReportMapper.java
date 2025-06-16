package com.sky.mapper;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public interface ReportMapper {

    /**
     * 营业额统计
     */
    Double turnoverStatistic(Map map);

    /**
     * 查询最近30天的数据
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO getStatistics(LocalDateTime begin, LocalDateTime end);
}
