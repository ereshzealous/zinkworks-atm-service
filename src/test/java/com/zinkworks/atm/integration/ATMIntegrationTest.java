package com.zinkworks.atm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zinkworks.atm.common.IErrorMessages;
import com.zinkworks.atm.common.IErrorStatus;
import com.zinkworks.atm.dto.NoteDenomination;
import com.zinkworks.atm.entity.Account;
import com.zinkworks.atm.entity.Notes;
import com.zinkworks.atm.models.WithDrawlRequest;
import com.zinkworks.atm.models.WithDrawlResponse;
import com.zinkworks.atm.repository.AccountRepository;
import com.zinkworks.atm.repository.NotesRepository;
import com.zinkworks.atm.service.ATMService;
import com.zinkworks.atm.util.TestDataGenerator;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {ATMIntegrationTest.Initializer.class})
public class ATMIntegrationTest {

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  NotesRepository notesRepository;

  @Autowired
  ATMService atmService;

  private String accountNumber_1;
  private Account account_1;
  private String accountNumber_2;
  private Account account_2;

  private List<Notes> notes;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Value("http://localhost:${local.server.port}")
  String baseUrl;

  @ClassRule
  public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres")
      .withDatabaseName("postgres")
      .withUsername("postgres")
      .withPassword("docker");

  @Test
  @SneakyThrows
  public void return_success_response_for_api_with_draw_cash_100() {
    generateSeedData();
    WithDrawlRequest request = new WithDrawlRequest();
    request.setAccountNumber(accountNumber_1);
    request.setPin(account_1.getPin().toString());
    request.setAmount(100d);
    String requestTxt = objectMapper.writeValueAsString(request);
    Response response = given()
        .header("Content-type", "application/json")
        .and()
        .body(requestTxt)
        .when()
        .post(baseUrl + "/api/cash/withdraw")
        .then()
        .extract().response();
    assertEquals(200, response.getStatusCode());
    assertEquals(account_1.getBalance() - 100d, response.jsonPath().getDouble("balance"));
    List<NoteDenomination> noteDenominations = response.body().jsonPath().getList("denominations", NoteDenomination.class);
    assertEquals(1, noteDenominations.size());
    assertEquals(50, noteDenominations.get(0).getNote());
    assertEquals(2, noteDenominations.get(0).getCount());
    tearDownSeedData();
  }

  @Test
  @SneakyThrows
  public void return_error_response_for_api_with_draw_cash_of_unavailability_of_balance() {
    generateSeedData();
    account_2.setBalance(500d);
    account_2.setOverdraft(10d);
    accountRepository.save(account_2);
    WithDrawlRequest request = new WithDrawlRequest();
    request.setAccountNumber(accountNumber_2);
    request.setPin(account_2.getPin().toString());
    request.setAmount(account_2.getBalance() + account_2.getOverdraft() + 100d);
    String requestTxt = objectMapper.writeValueAsString(request);
    Response response = given()
        .header("Content-type", "application/json")
        .and()
        .body(requestTxt)
        .when()
        .post(baseUrl + "/api/cash/withdraw")
        .then()
        .extract().response();
    assertEquals(400, response.getStatusCode());
    assertEquals("VALIDATION_ERROR", response.body().jsonPath().getString("code"));
    assertEquals(String.format(IErrorMessages.ACCOUNT_HAS_INSUFFICIENT_FUNDS, account_2.getAccountNumber()),
        response.body().jsonPath().getString("message"));
    tearDownSeedData();
  }

  @Test
  @SneakyThrows
  public void return_error_response_for_api_with_draw_cash_for_invalid_pin() {
    generateSeedData();
    WithDrawlRequest request = new WithDrawlRequest();
    request.setAccountNumber(accountNumber_2);
    request.setPin("0000");
    request.setAmount(200d);
    String requestTxt = objectMapper.writeValueAsString(request);
    Response response = given()
        .header("Content-type", "application/json")
        .and()
        .body(requestTxt)
        .when()
        .post(baseUrl + "/api/cash/withdraw")
        .then()
        .extract().response();
    assertEquals(400, response.getStatusCode());
    assertEquals("VALIDATION_ERROR", response.body().jsonPath().getString("code"));
    assertEquals(IErrorMessages.INVALID_PIN, response.body().jsonPath().getString("message"));
    tearDownSeedData();
  }

  @Test
  @SneakyThrows
  public void return_data_validation_for_api_with_draw_cash_for_method_arguments() {
    generateSeedData();
    WithDrawlRequest request = new WithDrawlRequest();
    request.setAccountNumber(RandomStringUtils.randomAlphabetic(10));
    request.setPin(account_2.getPin().toString());
    request.setAmount(200d);
    String requestTxt = objectMapper.writeValueAsString(request);
    Response response = given()
        .header("Content-type", "application/json")
        .and()
        .body(requestTxt)
        .when()
        .post(baseUrl + "/api/cash/withdraw")
        .then()
        .extract().response();
    assertEquals(400, response.getStatusCode());
    assertEquals("VALIDATION_ERROR", response.body().jsonPath().getString("code"));
    assertEquals("Invalid Account Number Format. Accept only Numbers.", response.body().jsonPath().getString("message"));
    tearDownSeedData();
  }

  @Test
  @SneakyThrows
  public void return_success_response_for_balance_enquiry_api() {
    generateSeedData();
    Response response = given()
        .header("Content-type", "application/json")
        .and()
        .when()
        .get(baseUrl + "/api/balance/"+account_2.getAccountNumber()+"?pin="+account_2.getPin())
        .then()
        .extract().response();
    assertEquals(200, response.getStatusCode());
    assertEquals(account_2.getBalance(), response.body().jsonPath().getDouble("balance"));
    assertEquals(account_2.getOverdraft(), response.body().jsonPath().getDouble("overdraft"));
    assertEquals(account_2.getOverdraft() + account_2.getBalance(), response.body().jsonPath().getDouble("totalWithDrawlLimit"));
    tearDownSeedData();
  }

  @Test
  @SneakyThrows
  public void return_validation_error_response_for_balance_enquiry_api_invalid_pin_format() {
    Response response = given()
        .header("Content-type", "application/json")
        .and()
        .when()
        .get(baseUrl + "/api/balance/12345?pin=000000")
        .then()
        .extract().response();
    assertEquals(400, response.getStatusCode());
    assertEquals("getBalance.pin: A four digit format is expected. For ex: 1234", response.body().jsonPath().getString("message"));
    assertEquals(IErrorStatus.DATA_VALIDATION, response.body().jsonPath().getString("code"));
  }

  public void generateSeedData() {
    accountNumber_1 = RandomStringUtils.randomNumeric(10);
    accountNumber_2 = RandomStringUtils.randomNumeric(10);
    account_1 = TestDataGenerator.generateAccount(accountNumber_1);
    account_1.setId(null);
    account_1 = accountRepository.save(account_1);

    account_2 = TestDataGenerator.generateAccount(accountNumber_2);
    account_2.setId(null);
    account_2 = accountRepository.save(account_2);

    notes = TestDataGenerator.generateNotes();
    notes = notes.stream().peek(v -> v.setId(null)).collect(Collectors.toList());
    notes = notesRepository.saveAll(notes);
  }

  public void tearDownSeedData() {
    accountRepository.deleteAll();
    notesRepository.deleteAll();
  }

  public static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.url=" + postgres.getJdbcUrl(),
          "spring.datasource.username=" + postgres.getUsername(),
          "spring.datasource.password=" + postgres.getPassword(),
          "spring.jpa.generate-ddl=true",
          "spring.jpa.hibernate.ddl-auto=create"
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}
