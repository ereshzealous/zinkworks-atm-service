package com.zinkworks.atm.service;

import com.zinkworks.atm.dto.AccountDTO;
import com.zinkworks.atm.dto.NoteDenomination;
import com.zinkworks.atm.entity.Notes;
import com.zinkworks.atm.models.BalanceEnquiryResponse;
import com.zinkworks.atm.models.WithDrawlRequest;
import com.zinkworks.atm.models.WithDrawlResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ATMService {

  private final AccountService accountService;
  private final NotesService notesService;

  @Transactional
  public WithDrawlResponse withDrawCash(WithDrawlRequest request) {
    AccountDTO accountDTO = accountService.validateAccountPin(request.getAccountNumber(), NumberUtils.toInt(request.getPin()));
    accountService.checkAmountForFunds(accountDTO, request.getAmount());
    List<Notes> notesAvailable = notesService.verifyATMForNotes(request.getAmount());
    List<NoteDenomination> noteDenominations = notesService.generateNotesDenomination(request.getAmount(), notesAvailable);
    Double balance = accountDTO.getBalance() - request.getAmount();
    accountService.updateAccount(accountDTO.getAccountNumber(), balance);
    notesService.updateNotes(noteDenominations, notesAvailable);
    return new WithDrawlResponse(noteDenominations.stream().filter(v -> !NumberUtils.INTEGER_ZERO.equals(v.getCount()))
        .collect(Collectors.toList()), balance);
  }

  public BalanceEnquiryResponse checkBalance(String accountNumber, String pin) {
    AccountDTO accountDTO = accountService.validateAccountPin(accountNumber, NumberUtils.toInt(pin));
    return new BalanceEnquiryResponse(accountDTO.getBalance(), accountDTO.getOverdraft(), accountDTO.getBalance() + accountDTO.getOverdraft());
  }
}
