package com.tech_hub.techhub.controller;
import com.tech_hub.techhub.dto.SalesDto;
import com.tech_hub.techhub.entity.Order;
import com.tech_hub.techhub.entity.Status;
import com.tech_hub.techhub.entity.TimePeriod;
import com.tech_hub.techhub.service.pdf_generator.PdfGeneratorService;
import com.tech_hub.techhub.service.sales_report.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    SalesReportService salesReportService;
    @Autowired
    PdfGeneratorService pdfGeneratorService;
    private static final String TOKEN_ATTRIBUTE = "token";

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAdminPage(Model model){

        SalesDto salesDataForOneDay = salesReportService.getSalesForOneDay();
        SalesDto salesDataForOneWeek = salesReportService.getSalesForOneWeek();
        SalesDto salesDataForOneMonth = salesReportService.getSalesForOneMonth();
        SalesDto salesDataForOneYear = salesReportService.getSalesForOneYear();
        SalesDto getAllSales = salesReportService.getSalesForAllTime();

        model.addAttribute("salesDataForOneDay",salesDataForOneDay);
        model.addAttribute("salesDataForOneWeek",salesDataForOneWeek);
        model.addAttribute("salesDataForOneMonth",salesDataForOneMonth);
        model.addAttribute("salesDataForOneYear",salesDataForOneYear);
        model.addAttribute("getAllSales",getAllSales);

        return "dashboard";
    }

    @GetMapping("/dashboard/graph")
    @ResponseBody
    public ResponseEntity<SalesDto>showGraph(){
        SalesDto getAllSales = salesReportService.getSalesForAllTime();
        return ResponseEntity.ok(getAllSales);
    }
    @GetMapping("/dashboard/daily-graph")
    @ResponseBody
    public ResponseEntity<SalesDto>showGraphWeekly(@RequestParam(value = "selectedPeriod")TimePeriod selectedPeriod){
        SalesDto salesDto= null;

        switch (selectedPeriod){
            case WEEKLY -> salesDto = salesReportService.getSalesForOneWeek();
            case MONTHLY -> salesDto = salesReportService.getSalesForOneMonth();
            case DAILY -> salesDto = salesReportService.getSalesForOneDay();

        }
        return ResponseEntity.ok(salesDto);
    }
    @PostMapping("/dashboard-sales")
    public String adminDashBoard(@RequestParam(value = "selectedTimePeriod")TimePeriod selectedTimePeriod,
                                 HttpSession session, Model model){

        if (selectedTimePeriod != null) {
            List<Order> orders = salesReportService.getOrderByTimePeriod(selectedTimePeriod);
            double totalSales = salesReportService.calculateTotalSales(orders);
            int totalOrders = orders.size();

            String token = UUID.randomUUID().toString();
            session.setAttribute(token, orders);

            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("orders", orders);
            model.addAttribute("totalSales", totalSales);
            model.addAttribute(TOKEN_ATTRIBUTE, token);
            return "sales-report";

        }else {
            model.addAttribute("message","Please select a time period");
            return "redirect:/admin/dashboard";
        }
    }
    @PostMapping("/dashboard/sales/byDate")
    public String orderByDates(@RequestParam(value = "startDate",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                        @RequestParam(value = "endDate",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                        HttpSession session, Model model){

        if (startDate != null && endDate != null) {
            List<Order> orders = salesReportService.findByOrderDateBetween(startDate, endDate);
            double totalSales = salesReportService.calculateTotalSales(orders);
            int totalOrders = orders.size();

            String token = UUID.randomUUID().toString();
            session.setAttribute(token, orders);

            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("orders", orders);
            model.addAttribute("totalSales", totalSales);
            model.addAttribute(TOKEN_ATTRIBUTE, token);
            return "sales-report";
        }else {
            model.addAttribute("message","please select dates");
            return "redirect:/admin/dashboard";
        }
    }
    @GetMapping("/generate-pdf")
    public void exportToPdf(HttpServletResponse response, HttpSession session,
                            HttpServletRequest request){

        String token = request.getParameter(TOKEN_ATTRIBUTE);
        List<Order> orders = (List<Order>) session.getAttribute(token);
        pdfGeneratorService.generatePdf(orders,response);

    }
    @GetMapping("/generate-csv")
    public void exportToCsv(HttpServletResponse response, HttpSession session,
                            HttpServletRequest request) throws IOException {

        String token = request.getParameter(TOKEN_ATTRIBUTE);
        List<Order> orders = (List<Order>) session.getAttribute(token);
        pdfGeneratorService.exportToCSV(orders,response);

    }

    @GetMapping("/statusCounts")
    @ResponseBody
    public ResponseEntity<Map<Status, Long>> getStatusCounts() {
        Map<Status, Long> statusCounts = salesReportService.getStatusCounts();
        return ResponseEntity.ok(statusCounts);
    }
}
