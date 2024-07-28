package com.tech_hub.techhub.service.sales_report;

import com.tech_hub.techhub.dto.SalesDto;
import com.tech_hub.techhub.entity.Order;
import com.tech_hub.techhub.entity.Status;
import com.tech_hub.techhub.entity.TimePeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SalesReportService {


    List<Order> getOrderByTimePeriod(TimePeriod timePeriod);
    Double calculateTotalSales(List<Order> orders);
    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
     SalesDto getSalesForOneDay();
     SalesDto getSalesForOneWeek();
     SalesDto getSalesForOneMonth();
     SalesDto getSalesForOneYear();
     SalesDto getSalesForAllTime();
     List<Status> getStatus();
    public Map<Status, Long> getStatusCounts();

}
