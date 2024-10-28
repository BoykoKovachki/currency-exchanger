package com.example.currencyexchanger.models;

public class CurrencyOwnedModel extends CurrencyModel {

    private String amount;

    public CurrencyOwnedModel(String name, String amount) {
        super.setName(name);
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
