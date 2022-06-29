package com.zinkworks.atm.util;

import com.zinkworks.atm.dto.NoteDenomination;
import com.zinkworks.atm.entity.Account;
import com.zinkworks.atm.entity.Notes;
import com.zinkworks.atm.models.BalanceEnquiryResponse;
import com.zinkworks.atm.models.WithDrawlRequest;
import com.zinkworks.atm.models.WithDrawlResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestDataGenerator {

  public static WithDrawlRequest generateWithDrawlRequest() {
    WithDrawlRequest request = new WithDrawlRequest();
    request.setAccountNumber(RandomStringUtils.randomNumeric(10));
    request.setPin(RandomStringUtils.randomNumeric(4));
    request.setAmount(NumberUtils.toDouble(RandomStringUtils.randomNumeric(3)));
    return request;
  }

  public static WithDrawlResponse generateWithDrawlResponse() {
    WithDrawlResponse response = new WithDrawlResponse();
    response.setBalance(NumberUtils.toDouble(RandomStringUtils.randomNumeric(4)));
    response.setDenominations(Collections.emptyList());
    return response;
  }

  public static BalanceEnquiryResponse generateBalanceEnquiryResponse(String accountNumber) {
    BalanceEnquiryResponse response = new BalanceEnquiryResponse();
    response.setBalance(NumberUtils.toDouble(RandomStringUtils.randomNumeric(5)));
    response.setOverdraft(NumberUtils.toDouble(RandomStringUtils.randomNumeric(3)));
    response.setTotalWithDrawlLimit(response.getBalance() + response.getOverdraft());
    return response;
  }

  public static List<Notes> generateNotes() {
    return Stream.of(generateNote(50, 10), generateNote(20, 30), generateNote(10, 30),
        generateNote(5, 20)).collect(Collectors.toList());
  }

  public static Notes generateNote(Integer note, Integer count) {
    Notes notes = new Notes();
    notes.setNote(note);
    notes.setCount(count);
    notes.setId(UUID.randomUUID());
    notes.setCreatedAt(LocalDateTime.now());
    notes.setUpdatedAt(LocalDateTime.now());
    return notes;
  }

  public static List<NoteDenomination> generateNoteDenominations() {
    return Stream.of(generateNoteDenomination(50, ThreadLocalRandom.current().nextInt(5)),
        generateNoteDenomination(20, ThreadLocalRandom.current().nextInt(5)),
        generateNoteDenomination(10, ThreadLocalRandom.current().nextInt(5)),
        generateNoteDenomination(5, ThreadLocalRandom.current().nextInt(5)))
        .collect(Collectors.toList());
  }

  public static NoteDenomination generateNoteDenomination(Integer note, Integer count) {
    NoteDenomination denomination = new NoteDenomination();
    denomination.setNote(note);
    denomination.setCount(count);
    return denomination;
  }

  public static Account generateAccount(String accountNumber) {
    Account account = new Account();
    account.setAccountNumber(StringUtils.isBlank(accountNumber) ? RandomStringUtils.randomAlphabetic(10) : accountNumber);
    account.setBalance(NumberUtils.toDouble(RandomStringUtils.randomNumeric(5)));
    account.setId(UUID.randomUUID());
    account.setOverdraft(NumberUtils.toDouble(RandomStringUtils.randomNumeric(3)));
    account.setPin(9999);
    account.setCreatedAt(LocalDateTime.now());
    account.setUpdatedAt(LocalDateTime.now());
    return account;
  }
}
