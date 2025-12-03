package com.samsepiol.file.nexus.content.data.parser;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.data.models.response.TudfFileContent;
import com.samsepiol.file.nexus.content.data.parser.config.TudfFileParserConfig;
import com.samsepiol.file.nexus.content.data.parser.impl.FieldMappings;
import com.samsepiol.file.nexus.content.data.parser.impl.TudfContentParser;
import com.samsepiol.file.nexus.content.data.parser.models.request.FileContentParsingRequest;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.library.core.exception.SerializationException;
import com.samsepiol.library.core.util.SerializationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FileContentParserTest {
    private static final String FILE_TYPE = "fileType";

    @Mock
    private FileSchemaConfig fileConfig;

    @InjectMocks
    private TudfContentParser tudfContentParser;

    @Test
    void testTUDFParsingTest() throws UnsupportedFileException, SerializationException {
        TudfFileParserConfig tudfFileParserConfig = TudfFileParserConfig.builder()
                .identifiers(Map.of('D', SerializationUtil.convertToEntity(fieldMappingList, FieldMappings.class)))
                .recordIdentifierIndex(50)
                .build();
        doReturn(tudfFileParserConfig)
                .when(fileConfig)
                .getParserConfig(FILE_TYPE);

        TudfFileContent tudfFileContent = TudfFileContent.builder()
                .payload(TudfFileContent.Payload.builder()
                        .message("                   10090300010090300000000489999ND1 1 000 00000000001 202419111999999998111999878905810002 00371 95020241911CASHBACK FOR EVERYTIME SPENDS                          31.41 C                 .00 000       100 999999998 P01                         000000000000000 19999999981119998789058000 000000000 00000 00000 000000000 CASHBACK EVERY0000000000000000000000000000                 .0               000100903XXXXXX0048                                  000000                                                                                                                                  000000         000000   000000000000                                                                                                                                                               .00000 999999.0000000                     000000                     000 00000000   00000000 000000000000 00000000                                  31.41 000000000      0000000000000000000000  00")
                        .rowNumber("1")
                        .build())
                .build();

        var parsedMap = assertDoesNotThrow(() -> tudfContentParser.parse(FileContentParsingRequest.of(FILE_TYPE, SerializationUtil.convertToString(tudfFileContent))));
        assertEquals("20241911", parsedMap.get("TransactionEffectiveDate"));
    }

    @Test
    void testTUDFParsingWithMessageAsEmptyLine() throws UnsupportedFileException, SerializationException {
        TudfFileParserConfig tudfFileParserConfig = TudfFileParserConfig.builder()
                .identifiers(Map.of('D', SerializationUtil.convertToEntity(fieldMappingList, FieldMappings.class)))
                .recordIdentifierIndex(50)
                .build();
        doReturn(tudfFileParserConfig)
                .when(fileConfig)
                .getParserConfig(FILE_TYPE);

        TudfFileContent tudfFileContent = TudfFileContent.builder()
                .payload(TudfFileContent.Payload.builder()
                        .message("")
                        .rowNumber("1")
                        .build())
                .build();

        var parsedMap = assertDoesNotThrow(() -> tudfContentParser.parse(FileContentParsingRequest.of(FILE_TYPE, SerializationUtil.convertToString(tudfFileContent))));
        assertTrue(parsedMap.isEmpty());
    }

    private static final String fieldMappingList = """
            {
              "fieldMapping": [
                {
                  "start": 1,
                  "end": 20,
                  "fieldName": "RelationshipNumber"
                },
                {
                  "start": 20,
                  "end": 26,
                  "fieldName": "OrganizationLogoNumber"
                },
                {
                  "start": 26,
                  "end": 45,
                  "fieldName": "AccountNumber"
                },
                {
                  "start": 45,
                  "end": 49,
                  "fieldName": "ZipSortCode"
                },
                {
                  "start": 49,
                  "end": 50,
                  "fieldName": "DuplicateStatementIndicator"
                },
                {
                  "start": 50,
                  "end": 51,
                  "fieldName": "RecordTypeIndicator"
                },
                {
                  "start": 51,
                  "end": 52,
                  "fieldName": "Category"
                },
                {
                  "start": 52,
                  "end": 53,
                  "fieldName": "CategorySign"
                },
                {
                  "start": 53,
                  "end": 54,
                  "fieldName": "Priority"
                },
                {
                  "start": 54,
                  "end": 55,
                  "fieldName": "PrioritySign"
                },
                {
                  "start": 55,
                  "end": 58,
                  "fieldName": "Form"
                },
                {
                  "start": 58,
                  "end": 59,
                  "fieldName": "FormSign"
                },
                {
                  "start": 59,
                  "end": 70,
                  "fieldName": "RecordSequence"
                },
                {
                  "start": 70,
                  "end": 71,
                  "fieldName": "RecordSequenceSign"
                },
                {
                  "start": 71,
                  "end": 79,
                  "fieldName": "TransactionEffectiveDate"
                },
                {
                  "start": 79,
                  "end": 102,
                  "fieldName": "TransactionReferenceNumber"
                },
                {
                  "start": 102,
                  "end": 107,
                  "fieldName": "TransactionPlanNumber"
                },
                {
                  "start": 107,
                  "end": 108,
                  "fieldName": "TransactionPlanNumberSign"
                },
                {
                  "start": 108,
                  "end": 113,
                  "fieldName": "TransactionCode"
                },
                {
                  "start": 113,
                  "end": 114,
                  "fieldName": "TransactionCodeSign"
                },
                {
                  "start": 114,
                  "end": 117,
                  "fieldName": "TransactionLogicModule"
                },
                {
                  "start": 117,
                  "end": 125,
                  "fieldName": "TransactionPostDate"
                },
                {
                  "start": 125,
                  "end": 165,
                  "fieldName": "TransactionDescription"
                },
                {
                  "start": 165,
                  "end": 186,
                  "fieldName": "TransactionAmount"
                },
                {
                  "start": 186,
                  "end": 187,
                  "fieldName": "TransactionType"
                },
                {
                  "start": 187,
                  "end": 208,
                  "fieldName": "TransactionUnitPrice"
                },
                {
                  "start": 208,
                  "end": 211,
                  "fieldName": "QuantityOfTransactionLimits"
                },
                {
                  "start": 211,
                  "end": 212,
                  "fieldName": "QuantityOfTransactionLimitsSign"
                },
                {
                  "start": 212,
                  "end": 218,
                  "fieldName": "TransactionAuthorizationCode"
                },
                {
                  "start": 218,
                  "end": 221,
                  "fieldName": "TransactionStoreOrganization"
                },
                {
                  "start": 221,
                  "end": 222,
                  "fieldName": "TransactionStoreOrganizationSign"
                },
                {
                  "start": 222,
                  "end": 231,
                  "fieldName": "TransactionStoreNumber"
                },
                {
                  "start": 231,
                  "end": 232,
                  "fieldName": "TransactionStoreNumberSign"
                },
                {
                  "start": 232,
                  "end": 244,
                  "fieldName": "TransactionSalespersonDetails"
                },
                {
                  "start": 244,
                  "end": 260,
                  "fieldName": "TransactionPurchaseOrderNumber"
                },
                {
                  "start": 260,
                  "end": 275,
                  "fieldName": "PaymentReferenceNumber"
                },
                {
                  "start": 275,
                  "end": 276,
                  "fieldName": "PaymentReferenceNumberSign"
                },
                {
                  "start": 276,
                  "end": 299,
                  "fieldName": "TransactionManagementSystemTramsReferenceNumber"
                },
                {
                  "start": 299,
                  "end": 302,
                  "fieldName": "MerchantOrganization"
                },
                {
                  "start": 302,
                  "end": 303,
                  "fieldName": "MerchantOrganizationSign"
                },
                {
                  "start": 303,
                  "end": 312,
                  "fieldName": "MerchantStore"
                },
                {
                  "start": 312,
                  "end": 313,
                  "fieldName": "MerchantStoreSign"
                },
                {
                  "start": 313,
                  "end": 318,
                  "fieldName": "CategoryCode"
                },
                {
                  "start": 318,
                  "end": 319,
                  "fieldName": "CategoryCodeSign"
                },
                {
                  "start": 319,
                  "end": 324,
                  "fieldName": "ProductGroup"
                },
                {
                  "start": 324,
                  "end": 325,
                  "fieldName": "ProductGroupSign"
                },
                {
                  "start": 325,
                  "end": 334,
                  "fieldName": "StockKeepingUnitNumber"
                },
                {
                  "start": 334,
                  "end": 335,
                  "fieldName": "StockKeepingUnitNumberSign"
                },
                {
                  "start": 335,
                  "end": 349,
                  "fieldName": "ReportDescription"
                },
                {
                  "start": 349,
                  "end": 364,
                  "fieldName": "VisaTransactionId"
                },
                {
                  "start": 364,
                  "end": 373,
                  "fieldName": "BankNetNumber"
                },
                {
                  "start": 373,
                  "end": 377,
                  "fieldName": "BankNetDate"
                },
                {
                  "start": 377,
                  "end": 396,
                  "fieldName": "InterchangeFee"
                },
                {
                  "start": 396,
                  "end": 411,
                  "fieldName": "TransactionTicketNumber"
                },
                {
                  "start": 411,
                  "end": 430,
                  "fieldName": "TransactionCardNumber"
                },
                {
                  "start": 430,
                  "end": 459,
                  "fieldName": "PassengerName"
                },
                {
                  "start": 459,
                  "end": 464,
                  "fieldName": "CityOfOrigin"
                },
                {
                  "start": 464,
                  "end": 470,
                  "fieldName": "TravelDate"
                },
                {
                  "start": 470,
                  "end": 475,
                  "fieldName": "CityOfDestination1"
                },
                {
                  "start": 475,
                  "end": 477,
                  "fieldName": "ServiceClass1"
                },
                {
                  "start": 477,
                  "end": 481,
                  "fieldName": "CarrierCode1"
                },
                {
                  "start": 481,
                  "end": 482,
                  "fieldName": "StopOverCode1"
                },
                {
                  "start": 482,
                  "end": 487,
                  "fieldName": "CityOfDestination2"
                },
                {
                  "start": 487,
                  "end": 489,
                  "fieldName": "ServiceClass2"
                },
                {
                  "start": 489,
                  "end": 493,
                  "fieldName": "CarrierCode2"
                },
                {
                  "start": 493,
                  "end": 494,
                  "fieldName": "StopOverCode2"
                },
                {
                  "start": 494,
                  "end": 499,
                  "fieldName": "CityOfDestination3"
                },
                {
                  "start": 499,
                  "end": 501,
                  "fieldName": "ServiceClass3"
                },
                {
                  "start": 501,
                  "end": 505,
                  "fieldName": "CarrierCode3"
                },
                {
                  "start": 505,
                  "end": 506,
                  "fieldName": "StopOverCode3"
                },
                {
                  "start": 506,
                  "end": 511,
                  "fieldName": "CityOfDestination4"
                },
                {
                  "start": 511,
                  "end": 513,
                  "fieldName": "ServiceClass4"
                },
                {
                  "start": 513,
                  "end": 517,
                  "fieldName": "CarrierCode4"
                },
                {
                  "start": 517,
                  "end": 518,
                  "fieldName": "StopOverCode4"
                },
                {
                  "start": 518,
                  "end": 543,
                  "fieldName": "ReturnCity"
                },
                {
                  "start": 543,
                  "end": 560,
                  "fieldName": "CustomerServicePhone"
                },
                {
                  "start": 560,
                  "end": 600,
                  "fieldName": "RenterName"
                },
                {
                  "start": 600,
                  "end": 606,
                  "fieldName": "CheckoutDate"
                },
                {
                  "start": 606,
                  "end": 615,
                  "fieldName": "AgreementNumber"
                },
                {
                  "start": 615,
                  "end": 621,
                  "fieldName": "ReturnDate"
                },
                {
                  "start": 621,
                  "end": 624,
                  "fieldName": "ReturnStateCountry"
                },
                {
                  "start": 624,
                  "end": 630,
                  "fieldName": "ArrivalDate"
                },
                {
                  "start": 630,
                  "end": 636,
                  "fieldName": "DepartureDate"
                },
                {
                  "start": 636,
                  "end": 661,
                  "fieldName": "FolioNumber"
                },
                {
                  "start": 661,
                  "end": 678,
                  "fieldName": "PropertyPhone"
                },
                {
                  "start": 678,
                  "end": 718,
                  "fieldName": "WebAddress"
                },
                {
                  "start": 718,
                  "end": 730,
                  "fieldName": "TransactionDescriptionInEuro"
                },
                {
                  "start": 730,
                  "end": 751,
                  "fieldName": "TransactionAmountInEuro"
                },
                {
                  "start": 751,
                  "end": 754,
                  "fieldName": "MerchantCountryCode"
                },
                {
                  "start": 754,
                  "end": 762,
                  "fieldName": "TravelAgencyCode"
                },
                {
                  "start": 762,
                  "end": 787,
                  "fieldName": "TravelAgencyName"
                },
                {
                  "start": 787,
                  "end": 801,
                  "fieldName": "StatementExchangeRate"
                },
                {
                  "start": 801,
                  "end": 802,
                  "fieldName": "StatementExchangeRateSign"
                },
                {
                  "start": 802,
                  "end": 805,
                  "fieldName": "TransactionSequence"
                },
                {
                  "start": 805,
                  "end": 808,
                  "fieldName": "QuarterlyAffiliateFlag"
                },
                {
                  "start": 808,
                  "end": 816,
                  "fieldName": "CurrentCompoundedRate"
                },
                {
                  "start": 816,
                  "end": 837,
                  "fieldName": "InstalmentTotalAmount"
                },
                {
                  "start": 837,
                  "end": 840,
                  "fieldName": "InstalmentCurrencyCode"
                },
                {
                  "start": 840,
                  "end": 843,
                  "fieldName": "InstalmentNumberOfPayments"
                },
                {
                  "start": 843,
                  "end": 864,
                  "fieldName": "InstalmentAmount"
                },
                {
                  "start": 864,
                  "end": 867,
                  "fieldName": "InstalmentPaymentNumber"
                },
                {
                  "start": 867,
                  "end": 868,
                  "fieldName": "InstalmentFrequency"
                },
                {
                  "start": 868,
                  "end": 876,
                  "fieldName": "SurchargeAmount"
                },
                {
                  "start": 876,
                  "end": 877,
                  "fieldName": "SurchargeAmountSign"
                },
                {
                  "start": 877,
                  "end": 879,
                  "fieldName": "SurchargeIndicator"
                },
                {
                  "start": 879,
                  "end": 887,
                  "fieldName": "SurchargeAmountCardholderCurrency"
                },
                {
                  "start": 887,
                  "end": 888,
                  "fieldName": "SurchargeAmountCardholderCurrencySign"
                },
                {
                  "start": 888,
                  "end": 900,
                  "fieldName": "MastercardSurchargeAmount"
                },
                {
                  "start": 900,
                  "end": 901,
                  "fieldName": "MastercardSurchargeAmountSign"
                },
                {
                  "start": 901,
                  "end": 909,
                  "fieldName": "MoneyTransferForeignExchangeFee"
                },
                {
                  "start": 909,
                  "end": 910,
                  "fieldName": "MoneyTransferForeignExchangeFeeSign"
                },
                {
                  "start": 910,
                  "end": 914,
                  "fieldName": "VatCode"
                },
                {
                  "start": 914,
                  "end": 928,
                  "fieldName": "ForeignTransactionAmount"
                },
                {
                  "start": 928,
                  "end": 949,
                  "fieldName": "LocalTxnAmount"
                },
                {
                  "start": 949,
                  "end": 958,
                  "fieldName": "MarkUpFee"
                },
                {
                  "start": 958,
                  "end": 959,
                  "fieldName": "MarkUpFeeSign"
                },
                {
                  "start": 959,
                  "end": 960,
                  "fieldName": "InstoreTxnFlag"
                },
                {
                  "start": 960,
                  "end": 963,
                  "fieldName": "FgnCurrCd"
                },
                {
                  "start": 963,
                  "end": 981,
                  "fieldName": "InvoiceRefNbr"
                },
                {
                  "start": 981,
                  "end": 986,
                  "fieldName": "OrigTxnCode"
                },
                {
                  "start": 986,
                  "end": 988,
                  "fieldName": "PosEntryMode"
                },
                {
                  "start": 988,
                  "end": 989,
                  "fieldName": "FppIndicator"
                },
                {
                  "start": 989,
                  "end": 990,
                  "fieldName": "DigitalCardIndicator"
                }
              ]
            }""";

}