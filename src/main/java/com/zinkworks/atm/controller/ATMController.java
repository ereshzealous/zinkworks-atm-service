package com.zinkworks.atm.controller;

import com.zinkworks.atm.exceptions.ApplicationException;
import com.zinkworks.atm.models.BalanceEnquiryResponse;
import com.zinkworks.atm.models.WithDrawlRequest;
import com.zinkworks.atm.models.WithDrawlResponse;
import com.zinkworks.atm.service.ATMService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(value = "The Resource Used For ATM Controller")
@Validated
public class ATMController {
  private final ATMService atmService;

  @ApiOperation(value = "With Draw Cash API", notes = "The API For Cash WithDrawl", response = WithDrawlResponse.class)
  @ApiResponses({
      @ApiResponse(code = 404, message = "Account Number Not Found", response = ApplicationException.class),
      @ApiResponse(code = 400, message = "Data Validation Exceptions like - Incorrect Pin, Insufficient Enough Funds, Invalid multiples of notes", response = ApplicationException.class),
      @ApiResponse(code = 500, message = "Un Expected Error", response = ApplicationException.class)
  })
  @PostMapping("/cash/withdraw")
  public ResponseEntity<WithDrawlResponse> withdrawCash(@Valid @RequestBody WithDrawlRequest request) {
    return ResponseEntity.ok(atmService.withDrawCash(request));
  }

  @ApiOperation(value = "Check Account Balance API", notes = "The API For Checking Account Balance", response = WithDrawlResponse.class)
  @ApiResponses({
      @ApiResponse(code = 404, message = "Account Number Not Found", response = ApplicationException.class),
      @ApiResponse(code = 400, message = "Data Validation Exceptions like - Incorrect Pin, Insufficient Enough Funds, Invalid multiples of notes", response = ApplicationException.class),
      @ApiResponse(code = 500, message = "Un Expected Error", response = ApplicationException.class)
  })
  @GetMapping("/balance/{account-number}")
  public ResponseEntity<BalanceEnquiryResponse> getBalance(@ApiParam(value = "account-number", required = true) @Pattern(regexp = "\\d+", message = "Invalid Account Number Format. Accept only Numbers.") @PathVariable("account-number") String accountNumber,
                                                           @ApiParam(value = "pin", required = true) @Pattern(regexp = "^\\d{4}$", message = "A four digit format is expected. For ex: 1234") @RequestParam(name = "pin") String pin) {
    return ResponseEntity.ok(atmService.checkBalance(accountNumber, pin));
  }
}
