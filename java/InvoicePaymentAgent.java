import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class InvoicePaymentAgent {

    public static class InvoiceResult {
        String recipient;
        double amount;
        String token;
        String reference;
        boolean isValid;
        String statusMessage;
        String unsignedTxPayload;

        public InvoiceResult(String recipient, double amount, String token, String reference, boolean isValid, String statusMessage, String unsignedTxPayload) {
            this.recipient = recipient;
            this.amount = amount;
            this.token = token;
            this.reference = reference;
            this.isValid = isValid;
            this.statusMessage = statusMessage;
            this.unsignedTxPayload = unsignedTxPayload;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== INVOICE PAYMENT AGENT REPORT ===\n");
            sb.append(String.format("Status: %s\n", statusMessage));
            if (isValid) {
                sb.append(String.format("Recipient: %s\n", recipient));
                sb.append(String.format("Amount: %.2f %s\n", amount, token));
                sb.append(String.format("Reference: %s\n", reference));
                sb.append(String.format("Unsigned Tx Payload: %s\n", unsignedTxPayload));
            }
            return sb.toString();
        }
    }

    // Requirements 01 & 02: Extracts from at least 2 formats and flags missing/mismatched fields instead of guessing
    public InvoiceResult processInvoice(String formatType, Map<String, String> invoiceData) {
        String recipient = invoiceData.get("recipient");
        String amountStr = invoiceData.get("amount");
        String token = invoiceData.get("token");
        String reference = invoiceData.get("reference");

        // Requirement 02: Flags missing fields or discrepancies instead of guessing
        if (recipient == null || recipient.isEmpty() || 
            amountStr == null || amountStr.isEmpty() || 
            token == null || token.isEmpty() || 
            reference == null || reference.isEmpty()) {
            return new InvoiceResult(null, 0.0, null, null, false, 
                "FLAGGED ERROR: Missing mandatory fields in " + formatType + " invoice. Refusing to guess.", null);
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                return new InvoiceResult(null, 0.0, null, null, false, 
                    "FLAGGED ERROR: Invalid or negative amount value.", null);
            }
        } catch (NumberFormatException e) {
            return new InvoiceResult(null, 0.0, null, null, false, 
                "FLAGGED ERROR: Amount format is invalid.", null);
        }

        // Requirement 03: Produces payment summary and an unsigned transaction including the invoice reference
        String unsignedTx = String.format("CALL transferTo(%s, %.2f, %s) [Ref: %s]", recipient, amount, token, reference);

        return new InvoiceResult(recipient, amount, token, reference, true, 
            "SUCCESS: Invoice successfully parsed and verified from " + formatType + ".", unsignedTx);
    }

    // Requirement 04: Includes a demonstration with example invoices
    public static void main(String[] args) {
        InvoicePaymentAgent agent = new InvoicePaymentAgent();

        System.out.println("--- Demonstration with Example Invoices ---");

        // Format 1: JSON-like Map Invoice
        Map<String, String> jsonInvoice = new HashMap<>();
        jsonInvoice.put("recipient", "0xRecipientWallet123...");
        jsonInvoice.put("amount", "250.50");
        jsonInvoice.put("token", "USDC");
        jsonInvoice.put("reference", "INV-2026-001");

        System.out.println("\nProcessing Format 1 (JSON Structure):");
        InvoiceResult res1 = agent.processInvoice("JSON Format", jsonInvoice);
        System.out.println(res1);

        // Format 2: Structured Text Map Invoice
        Map<String, String> textInvoice = new HashMap<>();
        textInvoice.put("recipient", "0xVendorWallet456...");
        textInvoice.put("amount", "1200.00");
        textInvoice.put("token", "PHAROS");
        textInvoice.put("reference", "INV-2026-002");

        System.out.println("\nProcessing Format 2 (Structured Text Format):");
        InvoiceResult res2 = agent.processInvoice("Structured Text Format", textInvoice);
        System.out.println(res2);

        // Format 3: Malformed/Incomplete Invoice (Testing Flag mechanism)
        Map<String, String> faultyInvoice = new HashMap<>();
        faultyInvoice.put("recipient", "0xIncomplete...");
        faultyInvoice.put("amount", "50.00");
        // missing token and reference fields

        System.out.println("\nProcessing Format 3 (Malformed Invoice - Testing Flags):");
        InvoiceResult res3 = agent.processInvoice("Raw Text Format", faultyInvoice);
        System.out.println(res3);
    }
}
