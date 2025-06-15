package com.sky.mapper;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public interface ReportMapper {

    /**
     * 营业额统计
     */
    Double turnoverStatistic(Map map);
}
