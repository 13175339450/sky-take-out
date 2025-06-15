package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {
    TurnoverReportVO turnoverStatistic(LocalDate begin, LocalDate end);
}
