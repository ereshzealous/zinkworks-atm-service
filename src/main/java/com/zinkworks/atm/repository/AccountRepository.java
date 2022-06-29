package com.zinkworks.atm.repository;

import com.zinkworks.atm.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface AccountRepository extends JpaRepository<Account, String> {
  Account findAccountByAccountNumber(String accountNumber);
}
