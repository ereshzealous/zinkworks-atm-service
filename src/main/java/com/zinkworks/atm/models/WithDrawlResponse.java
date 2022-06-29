package com.zinkworks.atm.models;

import com.zinkworks.atm.dto.NoteDenomination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithDrawlResponse {
  private List<NoteDenomination> denominations;
  private Double balance;
}
