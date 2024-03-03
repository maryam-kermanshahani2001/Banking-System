package com.example.demo;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

@Component
class TransactionLogger implements Observer {
    private final String logFilePath = "./transactions.text";

    public void onTransaction(String accountNumber, String transactionType, double amount) {
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            String logEntry = String.format("%s - %s: %.2f\n", accountNumber, transactionType, amount);
            writer.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Bank && arg instanceof Transaction) {
            Transaction transaction = (Transaction) arg;
            onTransaction(transaction.getAccountNumber(), transaction.getTransactionType(), transaction.getAmount());
        }
    }

}

