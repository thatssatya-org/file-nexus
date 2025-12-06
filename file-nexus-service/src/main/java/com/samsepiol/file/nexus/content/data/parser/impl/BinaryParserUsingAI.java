package com.samsepiol.file.nexus.content.data.parser.impl;

import com.samsepiol.file.nexus.content.data.parser.FileContentParser;
import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import com.samsepiol.file.nexus.content.data.parser.models.request.FileContentParsingRequest;
import com.samsepiol.file.nexus.content.exception.JsonFileContentParsingException;
import com.samsepiol.library.ai.models.enums.Model;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinaryParserUsingAI implements FileContentParser {
    private final Map<Model, ChatClient> chatClients;

    // TODO fetch this from config -> fileType to ai prompt
    private static final Model MODEL = Model.GEMINI;
    private static final String PROMPT = """
            Analyze the attached PDF statement. Extract data into the specified JSON format strictly.
            
            **Extraction Rules:**
            1. Dates: Convert all dates to strictly ISO-8601 format (YYYY-MM-DD).
            2. Merchants: Populate 'merchant_name_normalized' by removing transaction codes/IDs (e.g., 'RAZ*SWIGGY Bangalore' -> 'Swiggy').
            3. Currency: Identify the currency symbol (₹, $, €) and set the 'currency_code'.
            4. Loyalty: If the statement tracks Points, map them to 'loyalty_summary' with unit 'POINTS'. If Cashback, use unit 'CASHBACK'.
            5. Missing Data: If a field is not present in the PDF, omit it from the JSON.
            6. **ID Assignment:** Set the root-level 'id' field strictly to: {{TARGET_ID}}
            
            **Required Output Format:**
            You must output strictly compliant JSON matching this structure:
            
            {
              "id": "{{TARGET_ID}}",
              "metadata": {
                "currency_code": "INR",
                "language": "en-US"
              },
              "account_details": {
                "bank_name": "HDFC Bank",
                "product_name": "Regalia Gold",
                "account_holder_name": "John Doe",
                "account_number_masked": "xxxx-xxxx-xxxx-1234",
                "customer_email": "john.doe@example.com",
                "registered_address": "123 Baker Street, London"
              },
              "statement_summary": {
                "statement_date_iso": "2025-11-17",
                "billing_period": {
                  "start_date_iso": "2025-10-18",
                  "end_date_iso": "2025-11-17"
                },
                "payment_due_date_iso": "2025-12-07",
                "totals": {
                  "total_amount_due": 9005.13,
                  "minimum_amount_due": 420.00,
                  "credit_limit_total": 93000.00,
                  "credit_limit_available": 78961.00,
                  "previous_balance": 10190.20,
                  "new_purchases_total": 8285.00,
                  "payments_credits_total": 10910.48,
                  "finance_charges_total": 0.00
                }
              },
              "transactions": [
                {
                  "transaction_date_iso": "2025-10-31",
                  "posting_date_iso": "2025-11-01",
                  "description_raw": "RAZ*SWIGGY INSTAMART BANGALORE",
                  "merchant_name_normalized": "Swiggy Instamart",
                  "amount": 417.00,
                  "type": "debit",
                  "category_inferred": "Groceries",
                  "reference_number": "123456789",
                  "foreign_details": null
                },
                {
                  "transaction_date_iso": "2025-11-02",
                  "posting_date_iso": "2025-11-03",
                  "description_raw": "NETFLIX.COM AMSTERDAM",
                  "merchant_name_normalized": "Netflix",
                  "amount": 699.00,
                  "type": "debit",
                  "category_inferred": "Entertainment",
                  "foreign_details": {
                     "original_currency": "EUR",
                     "original_amount": 7.99,
                     "exchange_rate": 87.48
                  }
                },
                {
                  "transaction_date_iso": "2025-11-05",
                  "posting_date_iso": "2025-11-05",
                  "description_raw": "PAYMENT RECEIVED - THANK YOU",
                  "merchant_name_normalized": "Payment",
                  "amount": 10190.00,
                  "type": "credit",
                  "category_inferred": "Payment"
                }
              ],
              "loyalty_summary": {
                "program_name": "CashBack Rewards",
                "reward_unit": "CASHBACK",
                "opening_balance": 500.00,
                "earned_this_period": 53.18,
                "redeemed_this_period": 0.00,
                "closing_balance": 553.18
              }
            }""";
    private static final MediaType BINARY_STREAM_TYPE = MediaType.APPLICATION_PDF;
    private static final ChatOptions CHAT_OPTIONS = ChatOptions.builder().temperature(0D).build();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
    private static final String TARGET_ID = "{{TARGET_ID}}";
    private static final String UNIQUE_ID_FIELD = "customer_email";

    @Override
    public @NonNull Map<String, Object> parse(@NonNull FileContentParsingRequest request) throws JsonFileContentParsingException {
        var task = parsingSupplier(request);
        return CompletableFuture.supplyAsync(task, EXECUTOR_SERVICE).join();
    }

    @Override
    public @NonNull FileParserType getType() {
        return FileParserType.BINARY_USING_AI;
    }

    private Supplier<Map<String, Object>> parsingSupplier(@NotNull FileContentParsingRequest request) {
        return () -> {
            try {
                // TODO pass encoding in request
                var decodedString = Base64.getDecoder().decode(request.getContent());
                log.info("[BinaryParserUsingAI] Sending Request to AI Provider...");
                var response = chatClients.get(MODEL).prompt()
                        .user(userSpec -> userSpec
                                .text(PROMPT.replace(TARGET_ID, UNIQUE_ID_FIELD))
                                .media(BINARY_STREAM_TYPE, new ByteArrayResource(decodedString)))
                        .options(CHAT_OPTIONS)
                        .call()
                        .entity(new ParameterizedTypeReference<Map<String, Object>>() {});
                log.info("BinaryParserUsingAI] Response received:\n {} \n", response);
                return Objects.requireNonNullElse(response, Collections.emptyMap());
            } catch (Exception e) {
                throw JsonFileContentParsingException.create();
            }
        };
    }
}
