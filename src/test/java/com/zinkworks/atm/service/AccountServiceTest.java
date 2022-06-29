package com.zinkworks.atm.service;

import com.zinkworks.atm.common.IErrorMessages;
import com.zinkworks.atm.dto.AccountDTO;
import com.zinkworks.atm.entity.Account;
import com.zinkworks.atm.exceptions.DataValidationException;
import com.zinkworks.atm.exceptions.EntityNotFoundException;
import com.zinkworks.atm.repository.AccountRepository;
import com.zinkworks.atm.util.TestDataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  @InjectMocks
  private AccountService accountService;

  @Mock
  private AccountRepository mockAccountRepository;

  @BeforeEach
  public void init() {
    reset(mockAccountRepository);
  }

  @Test
  public void return_account_for_valid_number() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    Account account = TestDataGenerator.generateAccount(accountNumber);
    when(mockAccountRepository.findAccountByAccountNumber(eq(accountNumber))).thenReturn(account);
    AccountDTO actualAccount = accountService.getAccountByAccountNumber(accountNumber);
    assertNotNull(actualAccount);
    assertEquals(account.getAccountNumber(), actualAccount.getAccountNumber());
    assertEquals(account.getBalance(), actualAccount.getBalance());
    assertEquals(account.getOverdraft(), actualAccount.getOverdraft());
    assertEquals(account.getPin(), actualAccount.getPin());
    assertEquals(account.getId(), actualAccount.getId());
    assertEquals(account.getCreatedAt(), actualAccount.getCreatedAt());
    assertEquals(account.getUpdatedAt(), actualAccount.getUpdatedAt());
  }

  @Test
  public void should_throw_account_not_found_exception_for_invalid_number() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> accountService.getAccountByAccountNumber(accountNumber));
    assertNotNull(exception);
    assertEquals(String.format(IErrorMessages.ACCOUNT_NUMBER_NOT_FOUND, accountNumber), exception.getMessage());
  }

  @Test
  public void should_be_successful_for_checking_funds() {
    Account account = TestDataGenerator.generateAccount(null);
    AccountDTO accountDTO = new AccountDTO(account);
    accountDTO.setBalance(1000d);
    accountService.checkAmountForFunds(accountDTO, 100d);
  }

  @Test
  public void should_throw_error_when_no_account_details_passed_for_checking_availability_of_funds() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        accountService.checkAmountForFunds(null, 100d));
    assertNotNull(exception);
    assertEquals("The Account details passed are null.", exception.getMessage());
  }

  @Test
  public void should_throw_unavailability_of_funds_error_when_no_account_details_passed_for_checking_availability_of_funds() {
    Account account = TestDataGenerator.generateAccount(null);
    AccountDTO accountDTO = new AccountDTO(account);
    DataValidationException exception = assertThrows(DataValidationException.class, () ->
        accountService.checkAmountForFunds(accountDTO, accountDTO.getBalance() + accountDTO.getOverdraft() + 10d));
    assertNotNull(exception);
    assertEquals(String.format(IErrorMessages.ACCOUNT_HAS_INSUFFICIENT_FUNDS, account.getAccountNumber()), exception.getMessage());
  }

  @Test
  public void should_validate_pin_successful() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    Account account = TestDataGenerator.generateAccount(accountNumber);
    when(mockAccountRepository.findAccountByAccountNumber(eq(accountNumber))).thenReturn(account);
    AccountDTO actualAccount = accountService.validateAccountPin(accountNumber, account.getPin());
    assertNotNull(actualAccount);
    assertEquals(account.getAccountNumber(), actualAccount.getAccountNumber());
    assertEquals(account.getBalance(), actualAccount.getBalance());
    assertEquals(account.getOverdraft(), actualAccount.getOverdraft());
    assertEquals(account.getPin(), actualAccount.getPin());
    assertEquals(account.getId(), actualAccount.getId());
    assertEquals(account.getCreatedAt(), actualAccount.getCreatedAt());
    assertEquals(account.getUpdatedAt(), actualAccount.getUpdatedAt());
  }

  @Test
  public void should_throw_error_for_validating_pin() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    Account account = TestDataGenerator.generateAccount(accountNumber);
    when(mockAccountRepository.findAccountByAccountNumber(eq(accountNumber))).thenReturn(account);
    DataValidationException exception = assertThrows(DataValidationException.class, () ->
        accountService.validateAccountPin(accountNumber, ThreadLocalRandom.current().nextInt(4)));
    assertNotNull(exception);
    assertEquals(IErrorMessages.INVALID_PIN, exception.getMessage());
  }

  @Test
  public void should_successfully_update_account_details() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    Account account = TestDataGenerator.generateAccount(accountNumber);
    when(mockAccountRepository.findAccountByAccountNumber(eq(accountNumber))).thenReturn(account);
    accountService.updateAccount(accountNumber, 10d);
    verify(mockAccountRepository, times(1)).save(eq(account));
  }

  @Test
  public void should_throw_account_not_found_while_updating_for_invalid_account() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
        accountService.updateAccount(accountNumber, 10d));
    assertNotNull(exception);
    assertEquals(String.format(IErrorMessages.ACCOUNT_NUMBER_NOT_FOUND, accountNumber), exception.getMessage());
  }
}
