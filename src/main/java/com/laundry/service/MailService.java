package com.laundry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Async
    public void sendMail(String to,String subject,String otp){
        try{
            SimpleMailMessage mail=new SimpleMailMessage();
            mail.setFrom("Laundry Management System <sanjaypowerlaundry25@gmail.com>");
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(otp);
            javaMailSender.send(mail);
        } catch (Exception e) {
            System.out.println("Error!! Email NOT sent "+e);
        }


    }
}
