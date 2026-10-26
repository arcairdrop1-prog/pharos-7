# Pharos Invoice-to-Payment Agent

An intelligent agent developed in **Java** for the Pharos Agent Arena designed to read invoices from multiple formats, validate data integrity, flag discrepancies, and generate unsigned payment transactions[cite: 6].

## 📋 Requirements Met

1. **Multi-Format Extraction (Req. 01):** Extracts recipient, amount, token/currency, and reference from at least two distinct invoice formats[cite: 6].
2. **Flagging Mechanism (Req. 02):** Identifies missing fields or mismatches and flags errors safely instead of making assumptions[cite: 6].
3. **Payment Summary & Unsigned Tx (Req. 03):** Produces a complete payment summary alongside an unsigned transaction payload containing the invoice reference[cite: 6].
4. **Example Demonstration (Req. 04):** Includes a built-in demonstration with multiple example invoices (valid and malformed)[cite: 6].

## 🚀 How to Run

1. Certifique-se de ter o **Java JDK** instalado.
2. Compile o ficheiro:
   ```bash
   javac InvoicePaymentAgent.java
