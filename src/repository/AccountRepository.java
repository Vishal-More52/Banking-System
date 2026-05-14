package repository;

import domain.Account;

import java.util.*;

public class AccountRepository {

    private final Map<String, Account> accountsByNumber = new HashMap<>();

    public void save(Account account){
        accountsByNumber.put(account.getAccountNumber(),account);
    }

    public List<Account> findAll() {
        return  new ArrayList<>(accountsByNumber.values());
    }
}
