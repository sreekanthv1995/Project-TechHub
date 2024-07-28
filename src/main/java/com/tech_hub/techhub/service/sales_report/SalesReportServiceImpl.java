package com.tech_hub.techhub.service.sales_report;

import com.tech_hub.techhub.dto.SalesDto;
import com.tech_hub.techhub.entity.Order;
import com.tech_hub.techhub.entity.Status;
import com.tech_hub.techhub.entity.TimePeriod;
import com.tech_hub.techhub.repository.OrderRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesReportServiceImpl implements SalesReportService{

    @Autowired
    OrderRepository orderRepository;

    @Override
    public List<Order> getOrderByTimePeriod(TimePeriod timePeriod) {

        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (timePeriod){
            case DAILY -> {
                startDate = endDate;
                break;
            }
            case WEEKLY -> {
                startDate = endDate.minusDays(6);
                break;
            }
            case MONTHLY -> {
                startDate = endDate.minusMonths(1);
                break;
            }
            default -> {
                throw new IllegalArgumentException("unsupported time period " + timePeriod);
            }
        }
        return orderRepository.findByOrderDateBetween(startDate,endDate);
    }

    @Override
    public Double calculateTotalSales(List<Order> orders) {

        double totalPrice = 0;
        for (Order order : orders){
            totalPrice += order.getTotalPrice();
        }
        return totalPrice;
    }

    @Override
    public List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findByOrderDateBetween(startDate,endDate);
    }

    @Override
    public SalesDto getSalesForOneDay() {

        LocalDate yesterday = LocalDate.now();
        LocalDate today = LocalDate.now();

        List<Order> ordersWithInOneDay = orderRepository.findByOrderDateBetween(yesterday,today).stream().filter(order ->
                order.getStatus()!=Status.CANCELLED).toList();
        Map<LocalDate,Long> dailyOrderCount = ordersWithInOneDay.stream()
                .collect(Collectors.groupingBy(Order::getOrderDate,Collectors.counting()));

        Double totalRevenue = ordersWithInOneDay.stream().mapToDouble(Order::getTotalPrice).sum();
        Integer totalOrderCount = ordersWithInOneDay.size();

        return getSalesDto(yesterday, today, totalRevenue, totalOrderCount,dailyOrderCount);
    }

    @NotNull
    private static SalesDto getDto(LocalDate yesterday,
                                   LocalDate today, Double totalRevenue, Integer totalOrderCount,Map<LocalDate, Long> dailyOrderCount) {
        return getSalesDto(yesterday, today, totalRevenue, totalOrderCount, dailyOrderCount);

    }

    @Override
    public SalesDto getSalesForOneWeek() {

        LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
        LocalDate today = LocalDate.now();

        List<Order> ordersWithInOneWeek = orderRepository.findByOrderDateBetween(oneWeekAgo,today).
                stream().filter(order ->
                order.getStatus()!=Status.CANCELLED).toList();

        Map<LocalDate,Long> dailyOrderCount = ordersWithInOneWeek.stream()
                .collect(Collectors.groupingBy(Order::getOrderDate,Collectors.counting()));

        Double totalRevenue = ordersWithInOneWeek.stream().mapToDouble(Order::getTotalPrice).sum();
        Integer totalOrderCount = ordersWithInOneWeek.size();

        return getSalesDto(oneWeekAgo, today, dailyOrderCount, totalRevenue, totalOrderCount);

    }

    @NotNull
    private static SalesDto getSalesDto(LocalDate oneWeekAgo, LocalDate today,
                                        Map<LocalDate, Long> dailyOrderCount,
                                        Double totalRevenue, Integer totalOrderCount) {
        return getSalesDto(oneWeekAgo, today, totalRevenue, totalOrderCount, dailyOrderCount);
    }

    @Override
    public SalesDto getSalesForOneMonth() {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        LocalDate today = LocalDate.now();

        List<Order> ordersWithInOneMonth = orderRepository.findByOrderDateBetween(oneMonthAgo,today).stream().filter(order ->
                order.getStatus()!=Status.CANCELLED).toList();

        Double totalRevenue = ordersWithInOneMonth.stream().mapToDouble(Order::getTotalPrice).sum();
        Integer totalOrderCount = ordersWithInOneMonth.size();
        Map<LocalDate,Long> dailyOrderCount = ordersWithInOneMonth.stream()
                .collect(Collectors.groupingBy(Order::getOrderDate,Collectors.counting()));

        return getSalesDto(oneMonthAgo, today, totalRevenue, totalOrderCount, dailyOrderCount);

    }

    @NotNull
    private static SalesDto getSalesDto(LocalDate oneMonthAgo, LocalDate today, Double totalRevenue, Integer totalOrderCount, Map<LocalDate, Long> dailyOrderCount) {
        SalesDto salesDto = new SalesDto();
        salesDto.setStartDate(oneMonthAgo);
        salesDto.setEndDate(today);
        salesDto.setTotalOrderCount(totalOrderCount);
        salesDto.setTotalRevenue(totalRevenue);
        salesDto.setDailyOrderCounts(dailyOrderCount);
        return salesDto;
    }

    @Override
    public SalesDto getSalesForOneYear() {

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        LocalDate today = LocalDate.now();

        List<Order> ordersWithInOneYear = orderRepository.findByOrderDateBetween(oneYearAgo,today).stream().filter(order ->
                order.getStatus()!=Status.CANCELLED).toList();

        Double totalRevenue = ordersWithInOneYear.stream().mapToDouble(Order::getTotalPrice).sum();
        Integer totalOrderCount = ordersWithInOneYear.size();
        Map<LocalDate,Long> dailyOrderCount = ordersWithInOneYear.stream()
                .collect(Collectors.groupingBy(Order::getOrderDate,Collectors.counting()));

        return getSalesDto(oneYearAgo, today, totalRevenue, totalOrderCount,dailyOrderCount);
    }

//    @NotNull
//    private static SalesDto getSalesDto(LocalDate oneYearAgo, LocalDate today, Map<LocalDate, Long> dailyOrderCount,
//                                        Double totalRevenue, Integer totalOrderCount) {
//        SalesDto salesDto = new SalesDto();
//        salesDto.setStartDate(oneYearAgo);
//        salesDto.setEndDate(today);
//        salesDto.setTotalOrderCount(totalOrderCount);
//        salesDto.setTotalRevenue(totalRevenue);
//        salesDto.setDailyOrderCounts(dailyOrderCount);
//        return salesDto;
//    }

    @Override
    public SalesDto getSalesForAllTime() {

        List<Order> totalSales = orderRepository.findAll().stream().filter(order ->
                order.getStatus()!= Status.CANCELLED).toList();
        Map<String ,Double>monthlySales = new HashMap<>();

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        for (Order order : totalSales){
            LocalDate orderDate = order.getOrderDate();
            String monthYear = orderDate.format(monthFormatter);
            double totalPrice = order.getTotalPrice();

            monthlySales.put(monthYear,monthlySales.getOrDefault(monthYear, 0.0)+totalPrice);
        }
        List<String> months = new ArrayList<>(monthlySales.keySet());
        List<Double> amount  = new ArrayList<>(monthlySales.values());

        Double totalRevenue = amount.stream().mapToDouble(Double::doubleValue).sum();
        Integer totalCount = totalSales.size();

        return getSalesDto(months, amount, totalRevenue, totalCount);

    }

    @NotNull
    private static SalesDto getSalesDto(List<String> months, List<Double> amount,
                                        Double totalRevenue, Integer totalCount) {
        SalesDto salesDto = new SalesDto();
        salesDto.setTotalOrderCount(totalCount);
        salesDto.setTotalRevenue(totalRevenue);
        salesDto.setMonthlySales(months, amount);
        return salesDto;
    }

    @Override
    public List<Status> getStatus() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(Order::getStatus).toList();

    }

    @Override
    public Map<Status, Long> getStatusCounts() {
        List<Status> statusList = getStatus();
        Map<Status,Long> statusCount = new HashMap<>();
        for (Status status : statusList){
            Long count = orderRepository.countByStatus(status);
            statusCount.put(status,count);
        }
        return statusCount;
    }


}
