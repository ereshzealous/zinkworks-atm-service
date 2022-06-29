package com.zinkworks.atm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteDenomination {
  private Integer note;
  private Integer count = NumberUtils.INTEGER_ZERO;
}
