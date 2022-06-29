package com.zinkworks.atm.service;

import com.zinkworks.atm.common.IErrorMessages;
import com.zinkworks.atm.common.IErrorStatus;
import com.zinkworks.atm.dto.AccountDTO;
import com.zinkworks.atm.entity.Account;
import com.zinkworks.atm.exceptions.DataValidationException;
import com.zinkworks.atm.exceptions.EntityNotFoundException;
import com.zinkworks.atm.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.util.Assert.notNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;

  public AccountDTO getAccountByAccountNumber(String accountNumber) {
    Account account = Optional.ofNullable(accountRepository.findAccountByAccountNumber(accountNumber))
        .orElseThrow(() -> new EntityNotFoundException(String.format(IErrorMessages.ACCOUNT_NUMBER_NOT_FOUND, accountNumber),
            IErrorStatus.NOT_FOUND));
    return new AccountDTO(account);
  }

  public void checkAmountForFunds(AccountDTO account, Double requestedFund) {
    notNull(account, () -> "The Account details passed are null.");
    double totalFunds = account.getBalance() + account.getOverdraft();
    if (totalFunds < requestedFund) {
      throw new DataValidationException(String.format(IErrorMessages.ACCOUNT_HAS_INSUFFICIENT_FUNDS, account.getAccountNumber()),
          IErrorStatus.DATA_VALIDATION);
    }
  }

  public AccountDTO validateAccountPin(String accountNumber, Integer pin) {
    AccountDTO accountDTO = getAccountByAccountNumber(accountNumber);
    if (!pin.equals(accountDTO.getPin())) {
      throw new DataValidationException(IErrorMessages.INVALID_PIN, IErrorStatus.DATA_VALIDATION);
    }
    return accountDTO;
  }

  public void updateAccount(String accountNumber, Double newBalance) {
    Account account = accountRepository.findAccountByAccountNumber(accountNumber);
    if (account == null) {
      throw new EntityNotFoundException(String.format(IErrorMessages.ACCOUNT_NUMBER_NOT_FOUND, accountNumber), IErrorStatus.NOT_FOUND);
    } else {
      account.setBalance(newBalance);
      accountRepository.save(account);
    }
  }
}
