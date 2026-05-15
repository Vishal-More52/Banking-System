package app;

import domain.Account;
import domain.Transaction;
import exceptions.ValidationException;
import service.BankService;
import service.impl.BankServiceImpl;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankService bankService = new BankServiceImpl();

        ConsoleUI.printBanner();
        boolean running = true;

        while (running) {
            ConsoleUI.printMenu();
            ConsoleUI.prompt("Choose an option");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "0" -> {
                    running = false;
                    ConsoleUI.goodbye();
                }
                case "1" -> openAccount(scanner, bankService);
                case "2" -> deposit(scanner, bankService);
                case "3" -> withdraw(scanner, bankService);
                case "4" -> transfer(scanner, bankService);
                case "5" -> statement(scanner, bankService);
                case "6" -> listAccounts(bankService);
                case "7" -> searchAccounts(scanner, bankService);
                default -> ConsoleUI.error("Invalid option. Please choose 0-7.");
            }
        }
        scanner.close();
    }

    private static void openAccount(Scanner scanner, BankService bankService) {
        ConsoleUI.section("Open Account");
        ConsoleUI.prompt("Customer name");
        String name = scanner.nextLine().trim();
        ConsoleUI.prompt("Customer email");
        String email = scanner.nextLine().trim();
        ConsoleUI.prompt("Account type (SAVINGS / CURRENT)");
        String type = scanner.nextLine().trim();
        ConsoleUI.prompt("Initial deposit (optional, blank for 0)");
        String amountStr = scanner.nextLine().trim();

        double initial = 0;
        if (!amountStr.isEmpty()) {
            try {
                initial = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                ConsoleUI.warn("Invalid number; opening with $0.00 balance.");
            }
        }
        if (initial < 0) {
            ConsoleUI.warn("Initial deposit cannot be negative; using $0.00.");
            initial = 0;
        }

        try {
            String accountNumber = bankService.openAccount(name, email, type);
            if (initial > 0) {
                try {
                    bankService.deposit(accountNumber, initial, "Initial Deposit");
                } catch (ValidationException e) {
                    ConsoleUI.error(e.getMessage());
                }
            }
            ConsoleUI.success("Account opened: " + accountNumber);
        } catch (RuntimeException e) {
            ConsoleUI.error(e.getMessage());
        }
    }

    private static void deposit(Scanner scanner, BankService bankService) {
        ConsoleUI.section("Deposit");
        ConsoleUI.prompt("Account number");
        String accountNumber = scanner.nextLine().trim();
        ConsoleUI.prompt("Amount");
        String amountStr = scanner.nextLine().trim();

        try {
            double amount = Double.parseDouble(amountStr);
            bankService.deposit(accountNumber, amount, "Deposit");
            ConsoleUI.success("Deposited " + ConsoleUI.formatMoney(amount) + " into " + accountNumber);
        } catch (NumberFormatException e) {
            ConsoleUI.error("Invalid amount.");
        } catch (RuntimeException e) {
            ConsoleUI.error(e.getMessage());
        }
    }

    private static void withdraw(Scanner scanner, BankService bankService) {
        ConsoleUI.section("Withdraw");
        ConsoleUI.prompt("Account number");
        String accountNumber = scanner.nextLine().trim();
        ConsoleUI.prompt("Amount");
        String amountStr = scanner.nextLine().trim();

        try {
            double amount = Double.parseDouble(amountStr);
            bankService.withdraw(accountNumber, amount, "Withdrawal");
            ConsoleUI.success("Withdrew " + ConsoleUI.formatMoney(amount) + " from " + accountNumber);
        } catch (NumberFormatException e) {
            ConsoleUI.error("Invalid amount.");
        } catch (RuntimeException e) {
            ConsoleUI.error(e.getMessage());
        }
    }

    private static void transfer(Scanner scanner, BankService bankService) {
        ConsoleUI.section("Transfer");
        ConsoleUI.prompt("From account");
        String from = scanner.nextLine().trim();
        ConsoleUI.prompt("To account");
        String to = scanner.nextLine().trim();
        ConsoleUI.prompt("Amount");
        String amountStr = scanner.nextLine().trim();

        try {
            double amount = Double.parseDouble(amountStr);
            bankService.transfer(from, to, amount, "Transfer");
            ConsoleUI.success("Transferred " + ConsoleUI.formatMoney(amount) + " from " + from + " to " + to);
        } catch (NumberFormatException e) {
            ConsoleUI.error("Invalid amount.");
        } catch (ValidationException | RuntimeException e) {
            ConsoleUI.error(e.getMessage());
        }
    }

    private static void statement(Scanner scanner, BankService bankService) {
        ConsoleUI.prompt("Account number");
        String account = scanner.nextLine().trim();
        try {
            List<Transaction> transactions = bankService.getStatement(account);
            ConsoleUI.printStatement(account, transactions);
        } catch (RuntimeException e) {
            ConsoleUI.error(e.getMessage());
        }
    }

    private static void listAccounts(BankService bankService) {
        List<Account> accounts = bankService.listAccounts();
        ConsoleUI.printAccounts(accounts, "All Accounts");
    }

    private static void searchAccounts(Scanner scanner, BankService bankService) {
        ConsoleUI.section("Search Accounts");
        ConsoleUI.prompt("Customer name contains");
        String q = scanner.nextLine().trim();
        List<Account> results = bankService.searchAccountByCustomerName(q);
        if (results.isEmpty()) {
            ConsoleUI.info("No accounts found for \"" + q + "\".");
            return;
        }
        ConsoleUI.printAccounts(results, "Search Results");
    }
}
