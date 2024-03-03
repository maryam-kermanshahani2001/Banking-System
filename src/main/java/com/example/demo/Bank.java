package com.example.demo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
//@Slf4j
@Service
public class Bank extends Observable {
    //    @Autowired
    private final BankAccountRepository accountRepository;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    //    @Autowired
    private final TransactionLogger transactionLogger;

    @PostConstruct
    private void init() {
        addObserver(transactionLogger);
    }

//    public Bank() {
//        this.executorService = Executors.newFixedThreadPool(10);
//        addObserver(this.transactionLogger);
//    }

    //    public Bank(BankAccountRepository accountRepository) {
//        this.accountRepository = accountRepository;
//        this.executorService = Executors.newFixedThreadPool(10); // Adjust as needed
//        this.transactionLogger = new TransactionLogger("transaction.log");
//    }

    @Transactional
    public String createAccount(String accountHolder, double initialBalance) {
        BankAccount account = new BankAccount(accountHolder, initialBalance);
        accountRepository.save(account);
        setChanged();
        notifyObservers(new Transaction(accountHolder, "CREATE", initialBalance));
        return account.getAccountNumber();
    }

    @Transactional
    public void deposit(String accountNumber, double amount) {
        executorService.submit(() -> {
            BankAccount account = accountRepository.findByAccountNumber(accountNumber);
            System.out.println("from bank deposit function" + account);
            if (account != null) {
                account.deposit(amount);
                accountRepository.save(account);
                setChanged();
                notifyObservers(new Transaction(accountNumber, "DEPOSIT", amount));
            } else {
                System.out.println("Account not exist");
            }
        });
    }

    @Transactional
    public void withdraw(String accountNumber, double amount) {
        executorService.submit(() -> {
            System.out.println("Thread: " + Thread.currentThread().getName() + " - Starting withdrawal for account: " + accountNumber);
            BankAccount account = accountRepository.findByAccountNumber(accountNumber);
            if (account != null) {
                account.withdraw(amount);
                accountRepository.save(account);
                setChanged();
                notifyObservers(new Transaction(accountNumber, "WITHDRAWAL", amount));
                System.out.println("Thread: " + Thread.currentThread().getName() + " - Completed withdrawal for account: " + accountNumber);
            } else {
                System.out.println("Account not exist");
            }

        });
    }


    // Perform funds transfer between accounts
    @Transactional
    public void transfer(String sourceAccountNumber, String targetAccountNumber, double amount) {
        executorService.submit(() -> {
            BankAccount sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
            BankAccount targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);

            if (sourceAccount != null && targetAccount != null) {
                sourceAccount.withdraw(amount);
                targetAccount.deposit(amount);

                accountRepository.save(sourceAccount);
                accountRepository.save(targetAccount);

                setChanged();
                notifyObservers(new Transaction(sourceAccountNumber, "TRANSFER_OUT", amount));
                setChanged();
                notifyObservers(new Transaction(targetAccountNumber, "TRANSFER_IN", amount));
//                transactionLogger.logTransaction(sourceAccountNumber, "TRANSFER_OUT", amount);
//                transactionLogger.logTransaction(targetAccountNumber, "TRANSFER_IN", amount);
            } else if (sourceAccount == null) {
                System.out.println("Source Account not exist");
            } else {
                System.out.println("Target Account not exist");
            }
        });
    }

    // Display account balance
    public double getBalance(String accountNumber) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber);
        return (account != null) ? account.getBalance() : -1;
    }

    // Shutdown executor service when the application stops
    public void shutdown() {
        executorService.shutdown();
    }

}
