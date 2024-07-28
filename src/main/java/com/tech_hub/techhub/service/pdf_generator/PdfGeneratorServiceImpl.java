package com.tech_hub.techhub.service.pdf_generator;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tech_hub.techhub.dto.OrderCsvDto;
import com.tech_hub.techhub.entity.Order;
import com.tech_hub.techhub.service.sales_report.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService{

    @Autowired
    SalesReportService salesReportService;
    @Override
    public void generatePdf(List<Order> orders, HttpServletResponse response) {

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment;filename=salesReport.pdf";
        response.setHeader(headerKey,headerValue);

        try{
            Document document = new Document();
            PdfWriter.getInstance(document,response.getOutputStream());

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,12, BaseColor.BLACK);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA,10,BaseColor.BLACK);

            document.open();

            PdfPTable table = new PdfPTable(8);
            float[] columnWidths = {1f,3f, 3f, 4f, 3f, 3f, 3f,3f};
            table.setWidths(columnWidths);

            PdfPCell cell = new PdfPCell(new Phrase("Sales Report",headerFont));
            cell.setColspan(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            PdfPCell headerCell = new PdfPCell();
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setPadding(5);

            PdfPCell dataCell = new PdfPCell();
            dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            dataCell.setPadding(4);

            headerCell.setPhrase(new Phrase("SN",headerFont));
            table.addCell(headerCell);

            headerCell.setPhrase(new Phrase("Order ID", headerFont));
            table.addCell(headerCell);

            headerCell.setPhrase(new Phrase("User", headerFont));
            table.addCell(headerCell);

            headerCell.setPhrase(new Phrase("Product", headerFont));
            table.addCell(headerCell);

            headerCell.setPhrase(new Phrase("Order Date", headerFont));
            table.addCell(headerCell);

            headerCell.setPhrase(new Phrase("Status", headerFont));
            table.addCell(headerCell);

            headerCell.setPhrase(new Phrase("Price", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Payment", headerFont));
            table.addCell(headerCell);

            int sn = 1;
            for (Order order : orders) {
                String product = order.getOrderItems().stream().map(orderItem -> orderItem.getVariant()
                        .getProduct().getName()).collect(Collectors.joining());

                dataCell.setPhrase(new Phrase(String.valueOf(sn), cellFont));
                table.addCell(dataCell);
                sn++;
                dataCell.setPhrase(new Phrase(order.getOrderId(), cellFont));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(order.getUser().getUsername(), cellFont));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(product, cellFont));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(order.getOrderDate().toString(), cellFont));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(order.getStatus().toString(), cellFont));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(String.valueOf(order.getTotalPrice()), cellFont));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(String.valueOf(order.getPaymentMode()), cellFont));
                table.addCell(dataCell);
            }
            PdfPCell summaryCell = new PdfPCell(new Phrase("Summary", headerFont));
            summaryCell.setColspan(2);
            summaryCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(summaryCell);

            double totalSales = salesReportService.calculateTotalSales(orders);
            int totalOrders = orders.size();

            PdfPCell totalOrdersCell = new PdfPCell(new Phrase("Total Orders: " + totalOrders, cellFont));
            totalOrdersCell.setColspan(3);
            totalOrdersCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(totalOrdersCell);

            PdfPCell totalSalesCell = new PdfPCell(new Phrase("Total Sales: " + totalSales, cellFont));
            totalSalesCell.setColspan(3);
            totalSalesCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(totalSalesCell);
            document.add(table);
            document.close();

        }catch (DocumentException | IOException e){
            e.printStackTrace();
        }
    }

@Override
public void exportToCSV(List<Order> orders, HttpServletResponse response) throws IOException {
    response.setContentType("text/csv");
    String headerKey = "Content-Disposition";
    String headerValue = "attachment; attachment;filename=salesReport.csv";
    response.setHeader(headerKey, headerValue);

    ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
    String[] csvHeader = {"ORDER ID", "USER", "PRODUCT", "ORDER DATE", "STATUS", "PRICE", "PAYMENT"};
    String[] nameMapping = {"orderId", "username","productName","orderDate","status","totalPrice","paymentMode"};
    csvBeanWriter.writeHeader(csvHeader);

    List<OrderCsvDto> orderCsvDtoList = new ArrayList<>();
    for (Order order : orders) {
        String productName = order.getOrderItems().stream().map(orderItem -> orderItem.getVariant().getProduct().getName()).collect(Collectors.joining());
        OrderCsvDto orderCsvDto = new OrderCsvDto();
        orderCsvDto.setOrderId(order.getOrderId());
        orderCsvDto.setUsername(order.getUser().getUsername());
        orderCsvDto.setTotalPrice(order.getTotalPrice());
        orderCsvDto.setOrderDate(order.getOrderDate());
        orderCsvDto.setPaymentMode(String.valueOf(order.getPaymentMode()));
        orderCsvDto.setStatus(String.valueOf(order.getStatus()));
        orderCsvDto.setProductName(productName);

        orderCsvDtoList.add(orderCsvDto);

    }
    for (OrderCsvDto orderCsvDto :orderCsvDtoList){
        csvBeanWriter.write(orderCsvDto, nameMapping);
    }
    Double totalSales = salesReportService.calculateTotalSales(orders);
    csvBeanWriter.writeHeader("TOTAL SALES ",String.valueOf(totalSales));
    int totalOrderCount = orders.size();
    csvBeanWriter.writeHeader("TOTAL ORDER COUNT ",String.valueOf(totalOrderCount));

    csvBeanWriter.close();
}

}