package com.zinkworks.atm.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceEnquiryResponse {
  private Double balance;
  private Double overdraft;
  private Double totalWithDrawlLimit;
}
