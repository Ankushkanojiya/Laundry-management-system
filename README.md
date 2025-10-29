# 🧺 Laundry Management System

**Tech Stack:**  
Java • Spring Boot • MySQL • JWT Authentication • HTML/Vanilla JS • Docker • Render (Backend) • Vercel (Frontend)

---

## 📖 Overview

Laundry Management System is a complete web-based platform designed to manage laundry shop operations digitally.  
It allows **Admins** to manage customers, payments, and receipts, and enables **Customers** to track and pay bills online through **UPI**.

---

## 🚀 Features

### 👨‍💼 Admin
- Manage Customers (Add, Edit, Delete)
- Verify Pending Payments
- Generate & Download Receipts (PDF)
- Monitor Transaction History

### 👕 Customer
- View available services (Regular / Dry Cleaning)
- Make payments via **UPI Intent** (Note: payment app is opening but after enterd the PIN the transactions getting failed due to some security concerns)
- Download Payment Receipts
- Track service transactions

---

## 🐳 Docker Setup
- docker build -t laundry-backend .
- docker run -d -p 8080:8080 --name laundry-app laundry-backend
- Watch the video for reference: https://www.youtube.com/watch?v=jBDTsf8jsEs&t=1413s

