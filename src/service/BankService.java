package service;

import domain.Account;
import service.impl.BankServiceImpl;

import java.util.List;

public interface BankService  {
    String openAccount(String name, String email, String accountType);
    List<Account> listAccounts();

}
