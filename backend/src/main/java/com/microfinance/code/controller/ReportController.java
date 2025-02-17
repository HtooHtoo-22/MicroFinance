//package com.microfinance.code.controller;
//
//import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
//import net.sf.jasperreports.export.SimpleExporterInput;
//import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
//import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.sql.DataSource;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//public class ReportController {
//
//    @Autowired
//    private DataSource dataSource;
//
//    // Endpoint for PDF report
//    @GetMapping("/report/pdf")
//    public ResponseEntity<byte[]> generatePdfReport() throws Exception {
//        // Load and compile the Jasper report from the resources folder
//        InputStream reportStream = getClass().getResourceAsStream("/reports/CurrentAccount.jrxml");
//        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
//
//        // Add any parameters if required
//        Map<String, Object> parameters = new HashMap<>();
//        // e.g., parameters.put("paramName", value);
//
//        // Get a connection from the data source
//        try (Connection conn = dataSource.getConnection()) {
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
//
//            // Export the report to PDF
//            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_PDF);
//            headers.setContentDispositionFormData("inline", "report.pdf");
//
//            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//        }
//    }
//
//    // Endpoint for Excel report
//    @GetMapping("/report/excel")
//    public ResponseEntity<byte[]> generateExcelReport() throws Exception {
//        InputStream reportStream = getClass().getResourceAsStream("/reports/CurrentAccount.jrxml");
//        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
//
//        Map<String, Object> parameters = new HashMap<>();
//        // Add report parameters if needed
//
//        try (Connection conn = dataSource.getConnection();
//             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
//
//            // Configure and export to Excel
//            JRXlsxExporter exporter = new JRXlsxExporter();
//            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
//
//            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
//            configuration.setOnePagePerSheet(false); // Change as needed
//            exporter.setConfiguration(configuration);
//            exporter.exportReport();
//
//            byte[] excelBytes = baos.toByteArray();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDispositionFormData("attachment", "report.xlsx");
//
//            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
//        }
//    }
//}
