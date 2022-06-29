package com.zinkworks.atm.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;


@Data
@ApiModel(value = "With Draw Cash request")
public class WithDrawlRequest {

  @ApiModelProperty(value = "accountNumber", notes = "Account Number From Which Money can be Withdrawn", example = "123345")
  @NotBlank(message = "Account Number Is required.")
  @Pattern(regexp = "\\d+", message = "Invalid Account Number Format. Accept only Numbers.")
  private String accountNumber;

  @ApiModelProperty(value = "pin", notes = "ATM PIN that is required for the transaction", example = "1234")
  @NotBlank(message = "Pin is required")
  @Pattern(regexp = "^\\d{4}$", message = "A four digit format is expected. For ex: 1234")
  private String pin;

  @ApiModelProperty(value = "amount", notes = "Amount required to be withdrawn", example = "100.0")
  @NotNull(message = "amount is required")
  @Positive(message = "Can not Accept negative or zero values")
  @DecimalMax(value = "10000", message = "The withDrawl limit is 10000 per transaction.")
  private Double amount;
}
