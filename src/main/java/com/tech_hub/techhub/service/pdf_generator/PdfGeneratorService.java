package com.tech_hub.techhub.service.pdf_generator;

import com.tech_hub.techhub.entity.Order;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface PdfGeneratorService {

    void generatePdf(List<Order> orders, HttpServletResponse response);
    void exportToCSV(List<Order> orders ,HttpServletResponse response)throws IOException;

}
