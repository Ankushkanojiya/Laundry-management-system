package com.laundry.controller;

import com.laundry.model.PaymentTransactions;
import com.laundry.repo.PaymentTransactionHistory;
import com.laundry.service.ReceiptGeneratorPdf;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@RestController
public class ReceiptController {

    private final ReceiptGeneratorPdf generatePdf;

    @GetMapping("/{transactionId}/download")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long transactionId) {
        try{
            byte[] pdf= generatePdf.generateReceiptPdf(transactionId);
            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String fileName="receipt-"+transactionId+".pdf";
            headers.setContentDispositionFormData("attachment",fileName);

            return ResponseEntity.ok().headers(headers).body(pdf);
        }catch (Exception e){
            System.out.println("Failed to generate pdf in controller");
            return ResponseEntity.internalServerError().build();
        }
    }
}
