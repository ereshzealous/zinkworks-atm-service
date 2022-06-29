package com.zinkworks.atm.dto;

import com.zinkworks.atm.entity.Account;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AccountDTO {
  private UUID id;
  private String accountNumber;
  private Integer pin;
  private Double balance;
  private Double overdraft;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public AccountDTO(Account account) {
    this.accountNumber = account.getAccountNumber();
    this.balance = account.getBalance();
    this.id = account.getId();
    this.pin = account.getPin();
    this.overdraft = account.getOverdraft();
    this.createdAt = account.getCreatedAt();
    this.updatedAt = account.getUpdatedAt();
  }
}
