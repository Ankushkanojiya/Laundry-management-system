package com.laundry.controller;

import com.laundry.model.PaymentTransactions;
import com.laundry.repo.PaymentTransactionHistory;
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

    private final PaymentTransactionHistory paymentTransactionsHistory;

    @GetMapping("/{transactionId}/download")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable Long transactionId, HttpServletRequest request) {
        PaymentTransactions transaction = paymentTransactionsHistory.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        String pdfPath = transaction.getPdfPath();
        File pdfFile = new File(pdfPath);

        if (!pdfFile.exists()) {
            throw new RuntimeException("Receipt not generated yet for this transaction");
        }

        Resource resource = new FileSystemResource(pdfFile);

        String contentType = "application/pdf";
        String fileName = pdfFile.getName();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
