package com.example.currencyexchanger.utils;

import android.annotation.SuppressLint;

public class ExchangeCurrencyCalculator {

    private static final double commissionFee = 0.7f;

    private static final String defaultCurrency = "EUR";

    @SuppressLint("DefaultLocale")
    public static String exchangeCurrency(String fromCurrency, String toCurrency, String enteredAmount, String exchangeRate, String exchangeRateFrom) {
        if (enteredAmount.isEmpty()) {
            return "";
        }
        double amount = Double.parseDouble(enteredAmount);
        double rate = Double.parseDouble(exchangeRate);
        double rateFrom = Double.parseDouble(exchangeRateFrom);

        if (fromCurrency.equals(toCurrency)) {
            return String.format("%.4f", amount);
        }

        if (fromCurrency.equals(defaultCurrency)) {
            amount = amount * rate;
        } else if (toCurrency.equals(defaultCurrency)) {
            amount = amount / rateFrom;
        } else {
            amount = amount * (rate / rateFrom);
        }

        return String.format("%.4f", amount);
    }

    public static String subtractFromBalance(String currentBalance, String exchangedAmount, String commissionFee) {
        double amount = Double.parseDouble(currentBalance);
        double exchangedAmountValue = Double.parseDouble(exchangedAmount);
        if (!commissionFee.isEmpty()) {
            exchangedAmountValue = Double.sum(exchangedAmountValue, Double.parseDouble(commissionFee));
        }
        double newBalance = amount - exchangedAmountValue;

        return String.valueOf(newBalance);
    }

    @SuppressLint("DefaultLocale")
    public static String calcCommissionFee(String amount) {
        double balanceAmount = Double.parseDouble(amount);
        double commission = (balanceAmount * (commissionFee / 100.0f));

        return String.format("%.2f", commission);
    }

}
