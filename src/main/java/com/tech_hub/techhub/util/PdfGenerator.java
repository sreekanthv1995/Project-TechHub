//package com.tech_hub.TechHub.util;
//
//import com.itextpdf.html2pdf.ConverterProperties;
//import com.itextpdf.html2pdf.HtmlConverter;
//import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//
//@Service
//public class PdfGenerator {
//
//    public String htmlTOPdf(String processedHtml){
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        try{
//
//            PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
//            DefaultFontProvider defaultFontProvider = new DefaultFontProvider(false,true,false);
//            ConverterProperties converterProperties = new ConverterProperties();
//            converterProperties.setFontProvider(defaultFontProvider);
//            HtmlConverter.convertToPdf(processedHtml,pdfWriter,converterProperties);
//            FileOutputStream fileOutputStream = new FileOutputStream("");
//            byteArrayOutputStream.writeTo(fileOutputStream);
//            byteArrayOutputStream.close();
//            byteArrayOutputStream.flush();
//            fileOutputStream.close();
//            return null;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
