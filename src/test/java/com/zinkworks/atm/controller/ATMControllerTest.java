package com.zinkworks.atm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zinkworks.atm.common.IErrorMessages;
import com.zinkworks.atm.common.IErrorStatus;
import com.zinkworks.atm.exceptions.DataAccessException;
import com.zinkworks.atm.exceptions.DataValidationException;
import com.zinkworks.atm.exceptions.EntityNotFoundException;
import com.zinkworks.atm.models.BalanceEnquiryResponse;
import com.zinkworks.atm.models.WithDrawlRequest;
import com.zinkworks.atm.models.WithDrawlResponse;
import com.zinkworks.atm.service.ATMService;
import com.zinkworks.atm.util.TestDataGenerator;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ATMControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @MockBean
  private ATMService atmService;

  @BeforeEach
  public void init() {
    this.objectMapper = new ObjectMapper();
    Mockito.reset(atmService);
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_account_number_not_provided_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setAccountNumber(null);
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("Account Number Is required."));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_invalid_format_for_account_number_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setAccountNumber(RandomStringUtils.randomAlphabetic(10));
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("Invalid Account Number Format. Accept only Numbers."));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_empty_pin_passed_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setPin(StringUtils.EMPTY);
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_null_pin_passed_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setPin(null);
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("Pin is required"));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_limit_exceed_pin_passed_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setPin(RandomStringUtils.randomAlphabetic(6));
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("A four digit format is expected. For ex: 1234"));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_null_amount_passed_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setAmount(null);
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("amount is required"));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_zero_amount_passed_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setAmount(0d);
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("Can not Accept negative or zero values"));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_negative_amount_passed_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setAmount(-10d);
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("Can not Accept negative or zero values"));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_daily_limit_amount_exceeded_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    request.setAmount(1000000d);
    String requestBody = objectMapper.writeValueAsString(request);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("The withDrawl limit is 10000 per transaction."));
  }

  @SneakyThrows
  @Test
  public void should_return_successful_response_for_valid_inputs_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    String requestBody = objectMapper.writeValueAsString(request);
    WithDrawlResponse expectedResponse = TestDataGenerator.generateWithDrawlResponse();
    when(atmService.withDrawCash(eq(request))).thenReturn(expectedResponse);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(expectedResponse.getBalance()));
  }

  @SneakyThrows
  @Test
  public void should_throw_un_expected_error_for_exceptions_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    String requestBody = objectMapper.writeValueAsString(request);
    when(atmService.withDrawCash(eq(request))).thenThrow(new RuntimeException("unexpected exception"));
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isInternalServerError());
  }

  @SneakyThrows
  @Test
  public void should_throw_data_access_exceptions_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    DataAccessException exception = new DataAccessException(IErrorMessages.REQUESTED_AMOUNT_IS_NOT_AVAILABLE_IN_ATM, IErrorStatus.INTERNAL_SERVER_ERROR);
    String requestBody = objectMapper.writeValueAsString(request);
    when(atmService.withDrawCash(eq(request))).thenThrow(exception);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(IErrorMessages.REQUESTED_AMOUNT_IS_NOT_AVAILABLE_IN_ATM))
        .andExpect(jsonPath("$.code").value(IErrorStatus.DAO_EXCEPTION));
  }

  @SneakyThrows
  @Test
  public void should_throw_entity_not_found_exceptions_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    EntityNotFoundException exception = new EntityNotFoundException(String.format(IErrorMessages.ACCOUNT_NUMBER_NOT_FOUND, "1235"), IErrorStatus.NOT_FOUND);
    String requestBody = objectMapper.writeValueAsString(request);
    when(atmService.withDrawCash(eq(request))).thenThrow(exception);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(String.format(IErrorMessages.ACCOUNT_NUMBER_NOT_FOUND, "1235")))
        .andExpect(jsonPath("$.code").value(IErrorStatus.NOT_FOUND));
  }

  @SneakyThrows
  @Test
  public void should_throw_data_validation_exceptions_for_withDrawl() {
    WithDrawlRequest request = TestDataGenerator.generateWithDrawlRequest();
    DataValidationException exception = new DataValidationException(String.format(IErrorMessages.ACCOUNT_HAS_INSUFFICIENT_FUNDS, "1235"), IErrorStatus.DATA_VALIDATION);
    String requestBody = objectMapper.writeValueAsString(request);
    when(atmService.withDrawCash(eq(request))).thenThrow(exception);
    mockMvc.perform(post("/api/cash/withdraw").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(String.format(IErrorMessages.ACCOUNT_HAS_INSUFFICIENT_FUNDS, "1235")))
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_invalid_account_format_for_get_balance() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    String pin = RandomStringUtils.randomNumeric(4);
    mockMvc.perform(get("/api/balance/"+accountNumber+"?pin="+pin)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("getBalance.accountNumber: Invalid Account Number Format. Accept only Numbers."));
  }

  @SneakyThrows
  @Test
  public void should_throw_validation_error_for_invalid_pin_format_for_get_balance() {
    String accountNumber = RandomStringUtils.randomNumeric(10);
    String pin = RandomStringUtils.randomNumeric(1);
    mockMvc.perform(get("/api/balance/"+accountNumber+"?pin="+pin)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(IErrorStatus.DATA_VALIDATION))
        .andExpect(jsonPath("$.message").value("getBalance.pin: A four digit format is expected. For ex: 1234"));
  }

  @SneakyThrows
  @Test
  public void should_return_success_response_for_get_balance() {
    String accountNumber = RandomStringUtils.randomNumeric(10);
    String pin = RandomStringUtils.randomNumeric(4);
    BalanceEnquiryResponse response = TestDataGenerator.generateBalanceEnquiryResponse(accountNumber);
    when(atmService.checkBalance(eq(accountNumber), eq(pin))).thenReturn(response);
    mockMvc.perform(get("/api/balance/"+accountNumber+"?pin="+pin)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(response.getBalance()))
        .andExpect(jsonPath("$.overdraft").value(response.getOverdraft()))
        .andExpect(jsonPath("$.totalWithDrawlLimit").value(response.getTotalWithDrawlLimit()));
  }
}
