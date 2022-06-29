package com.zinkworks.atm.service;

import com.zinkworks.atm.common.IErrorMessages;
import com.zinkworks.atm.dto.AccountDTO;
import com.zinkworks.atm.entity.Account;
import com.zinkworks.atm.entity.Notes;
import com.zinkworks.atm.exceptions.DataValidationException;
import com.zinkworks.atm.models.BalanceEnquiryResponse;
import com.zinkworks.atm.models.WithDrawlRequest;
import com.zinkworks.atm.models.WithDrawlResponse;
import com.zinkworks.atm.util.TestDataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ATMServiceTest {

  @InjectMocks
  private ATMService atmService;

  @Mock
  private NotesService mockNotesService;

  @Mock
  private AccountService mockAccountService;

  @BeforeEach
  public void init() {
    reset(mockAccountService, mockNotesService);
  }

  @Test
  public void should_return_cash_with_drawl_of_100_success() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    Account account = TestDataGenerator.generateAccount(accountNumber);
    AccountDTO accountDTO = new AccountDTO(account);
    List<Notes> availableNotes = TestDataGenerator.generateNotes();
    Double amount = 100d;
    when(mockAccountService.validateAccountPin(eq(accountNumber), eq(account.getPin()))).thenReturn(accountDTO);
    when(mockNotesService.verifyATMForNotes(eq(amount))).thenReturn(availableNotes);
    when(mockNotesService.generateNotesDenomination(eq(amount), eq(availableNotes))).thenCallRealMethod();
    WithDrawlRequest request = new WithDrawlRequest();
    request.setAmount(amount);
    request.setPin(account.getPin().toString());
    request.setAccountNumber(accountNumber);

    WithDrawlResponse response = atmService.withDrawCash(request);

    assertNotNull(response);
    assertEquals(accountDTO.getBalance() - 100d, response.getBalance());
    assertNotNull(response.getDenominations());
    assertEquals(50, response.getDenominations().get(0).getNote());
    assertEquals(2, response.getDenominations().get(0).getCount());
    verify(mockAccountService, times(1)).updateAccount(eq(accountNumber), eq(accountDTO.getBalance() - 100d));
    verify(mockNotesService, times(1)).updateNotes(any(), any());
  }

  @Test
  public void should_throw_error_while_withdrawing__209_cash() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    Account account = TestDataGenerator.generateAccount(accountNumber);
    AccountDTO accountDTO = new AccountDTO(account);
    List<Notes> availableNotes = TestDataGenerator.generateNotes();
    Double amount = 209d;
    when(mockAccountService.validateAccountPin(eq(accountNumber), eq(account.getPin()))).thenReturn(accountDTO);
    when(mockNotesService.verifyATMForNotes(eq(amount))).thenReturn(availableNotes);
    when(mockNotesService.generateNotesDenomination(eq(amount), eq(availableNotes))).thenCallRealMethod();WithDrawlRequest request = new WithDrawlRequest();
    request.setAmount(amount);
    request.setPin(account.getPin().toString());
    request.setAccountNumber(accountNumber);

    DataValidationException exception = assertThrows(DataValidationException.class, () -> atmService.withDrawCash(request));
    assertNotNull(exception);
    assertEquals(IErrorMessages.UNAVAILABILITY_OF_DENOMINATION, exception.getMessage());
  }

  @Test
  public void should_return_balance_enquiry_successfully() {
    String accountNumber = RandomStringUtils.randomAlphabetic(10);
    Account account = TestDataGenerator.generateAccount(accountNumber);
    AccountDTO accountDTO = new AccountDTO(account);
    when(mockAccountService.validateAccountPin(eq(accountNumber), eq(account.getPin()))).thenReturn(accountDTO);
    BalanceEnquiryResponse response = atmService.checkBalance(accountNumber, accountDTO.getPin().toString());
    assertNotNull(response);
    assertEquals(account.getBalance(), response.getBalance());
    assertEquals(account.getOverdraft(), response.getOverdraft());
    assertEquals(account.getOverdraft() + account.getBalance(), response.getTotalWithDrawlLimit());
  }

}
