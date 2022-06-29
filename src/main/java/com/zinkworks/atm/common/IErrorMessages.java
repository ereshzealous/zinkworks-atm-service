package com.zinkworks.atm.common;

public interface IErrorMessages {
  String UN_EXPECTED_ERROR ="Unexpected Error Occurred";
  String ACCOUNT_NUMBER_NOT_FOUND = "The Account Number => %s, not found";
  String ACCOUNT_HAS_INSUFFICIENT_FUNDS = "The Account Number => %s, didn't had enough cash to dispense";
  String INVALID_PIN = "The provided PIN is not correct";
  String REQUESTED_AMOUNT_IS_NOT_AVAILABLE_IN_ATM = "ATM balance is low. Can't dispense the requested amount.";
  String UNAVAILABILITY_OF_DENOMINATION = "ATM Doesn't had requested amount in denominations";
}
