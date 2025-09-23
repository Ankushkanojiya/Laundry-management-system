package com.laundry.service;

import com.laundry.dto.ReceiptDataDTO;
import com.laundry.exception.PaymentTransactionIdNotFound;
import com.laundry.model.PaymentTransactions;
import com.laundry.repo.PaymentTransactionHistory;
import com.lowagie.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
@AllArgsConstructor
public class ReceiptGeneratorPdf {

    private PaymentTransactionHistory transactionRepo;
    private TemplateEngine templateEngine;

    public byte[] generateReceiptPdf(Long transactionId) throws DocumentException{
        PaymentTransactions transaction=transactionRepo.findById(transactionId)
                .orElseThrow(()-> new PaymentTransactionIdNotFound(transactionId));

        ReceiptDataDTO receiptData= ReceiptDataDTO.builder()
                .transactionId(transaction.getTransactionId())
                .customerName(transaction.getAccount().getCustomer().getName())
                .paymentDate(transaction.getTimestamp())
                .amount(transaction.getAmount())
                .build();
        System.out.println("DTO has been initialized");

        Context context=new Context();
        context.setVariable("data",receiptData);
        System.out.println("Context has been set");
        String html=templateEngine.process("receipt-template",context);
        System.out.println("processed the html");
        //convert html to flying saucer

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        ITextRenderer renderer=new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream , false);
        renderer.finishPDF();
        System.out.println("pdf is generated");
        return outputStream.toByteArray();
    }


}
