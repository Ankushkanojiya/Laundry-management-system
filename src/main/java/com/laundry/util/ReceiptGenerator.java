package com.laundry.util;

import com.laundry.model.PaymentTransactions;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    private static final String RECEIPT_DIR = "receipts/";

    public static String generateReceipt(PaymentTransactions txn) throws IOException {
        File dir = new File(RECEIPT_DIR);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "receipt_" + txn.getTimestamp().toLocalDate() + "_" + txn.getTransactionId() + ".pdf";
        File file = new File(dir, fileName);

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(300,400));
        document.addPage(page);

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.setLeading(15f); // Line spacing

            content.beginText();
            content.newLineAtOffset(30, 360);

            // Header
            content.showText("-----------------------------------------------------");
            content.newLine();
            content.newLine();
            content.newLineAtOffset(25, 0); // Slight indent to center title
            content.showText("LAUNDRY SHOP");
            content.newLineAtOffset(-25, 0); // Reset indent
            content.newLine();
            content.showText("------------------------------------------------------");
            content.newLine();

            // Body
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLine();
            content.showText("Receipt No:  " + String.format("%02d", txn.getTransactionId()));
            content.newLine();
            content.showText("Customer  : " + txn.getAccount().getCustomer().getName());

            String dateTime = txn.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a"));
            content.newLine();
            content.showText("Date         : " + dateTime);
            content.newLine();
            content.newLine();

            content.showText("Amount Paid          : Rs." + String.format("%.2f", txn.getAmount()));
            double balance = txn.getAccount().getBalance();
            content.newLine();
            content.showText("Remaining Balance  : Rs." + String.format("%.2f", balance));
            content.newLine();
            content.newLine();

            content.showText("Thank you for your payment!");
            content.newLine();
            content.showText("-------------------------------");

            content.endText();
        }




        document.save(file);

        return file.getAbsolutePath();
    }
}