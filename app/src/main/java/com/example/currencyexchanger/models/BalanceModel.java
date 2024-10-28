package com.example.currencyexchanger.models;

import java.util.ArrayList;

public class BalanceModel {

    private static BalanceModel instance = null;

    private ArrayList<CurrencyOwnedModel> balance;

    private int noCommissionFeeExchanges = 5;
    private final int noCommissionFeeCounterGoal = 10;
    private int noCommissionFeeExchangeCounter;

    public static BalanceModel getInstance() {
        if (instance == null) {
            instance = new BalanceModel();
        }
        return instance;
    }

    public BalanceModel() {
        balance = new ArrayList<>();
        addToBalanceDefault();
    }

    public ArrayList<CurrencyOwnedModel> getBalance() {
        return balance;
    }

    private void addToBalanceDefault() {
        balance.add(new CurrencyOwnedModel("EUR", "1000"));
    }

    public int getNoCommissionFeeExchanges() {
        return noCommissionFeeExchanges;
    }

    public String getBalanceToString() {
        String balance = "";

        for (CurrencyOwnedModel model : this.balance) {
            double amountVal = Double.parseDouble(model.getAmount());
            String amount = String.format("%.2f", amountVal);

            if (this.balance.size() == 1) {
                balance = String.format("%s %s", amount, model.getName());
            } else {
                if (balance.isEmpty()) {
                    balance = String.format("%s %s", amount, model.getName());
                } else {
                    balance = String.format("%s %s %s %s", balance, "    ", amount, model.getName());
                }
            }
        }

        return balance;
    }

    public void updateBalance(String currencyName, String newAmount) {
        for (CurrencyOwnedModel model : this.balance) {
            if (model.getName().equals(currencyName)) {
                model.setAmount(newAmount);
                break;
            }
        }
    }

    public String getSpecificAmountFromBalance(String currencyName) {
        for (CurrencyOwnedModel model : this.balance) {
            if (model.getName().equals(currencyName)) {
                return model.getAmount();
            }
        }
        return "";
    }

    public void addCurrencyToBalance(CurrencyOwnedModel currency) {
        for (CurrencyOwnedModel model : this.balance) {
            if (model.getName().equals(currency.getName())) {
                double newBalance = Double.sum(Double.parseDouble(model.getAmount()), Double.parseDouble(currency.getAmount()));
                model.setAmount(String.valueOf(newBalance));
                return;
            }
        }

        this.balance.add(currency);
    }

    public void incrementNoCommissionFeeExchangeCounter() {
        if (noCommissionFeeExchangeCounter == noCommissionFeeCounterGoal) {
            noCommissionFeeExchanges++;
            noCommissionFeeExchangeCounter = 0;
        }
        noCommissionFeeExchangeCounter++;
    }

    public void decrementNoCommissionFeeParam() {
        if (noCommissionFeeExchanges > 0) {
            noCommissionFeeExchanges--;
        }
    }

}
