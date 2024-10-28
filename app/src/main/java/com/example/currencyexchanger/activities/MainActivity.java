package com.example.currencyexchanger.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.currencyexchanger.R;
import com.example.currencyexchanger.models.BalanceModel;
import com.example.currencyexchanger.models.CurrencyExchangeRateModel;
import com.example.currencyexchanger.models.CurrencyModel;
import com.example.currencyexchanger.models.CurrencyOwnedModel;
import com.example.currencyexchanger.models.ResponseModel;
import com.example.currencyexchanger.services.ServerCommunication;
import com.example.currencyexchanger.utils.ExchangeCurrencyCalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int timeForRequest = 5000;

    private static boolean firstLoad = true;

    private ArrayList<CurrencyExchangeRateModel> currencyExchangeRates = new ArrayList<>();

    private CurrencyOwnedModel selectedCurrencyForExchangeToSell;
    private CurrencyExchangeRateModel selectedCurrencyForExchangeToReceive;

    private TextView balanceTextView;
    private EditText sellEditText;
    private Spinner sellExchangeRateSpinner;
    private EditText receiveEditText;
    private Spinner receiveExchangeRateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout mainLayout = findViewById(R.id.mainLayout);
        balanceTextView = findViewById(R.id.balanceTextView);
        sellEditText = findViewById(R.id.sellEditText);
        sellExchangeRateSpinner = findViewById(R.id.sellExchangeRateSpinner);
        receiveEditText = findViewById(R.id.receiveEditText);
        receiveExchangeRateSpinner = findViewById(R.id.receiveExchangeRateSpinner);
        Button submitButton = findViewById(R.id.submitButton);

        sellEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateReceiverEditText();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sellExchangeRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCurrencyForExchangeToSell = BalanceModel.getInstance().getBalance().get(i);

                if (!receiveEditText.getText().toString().isEmpty()) {
                    updateReceiverEditText();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        receiveExchangeRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCurrencyForExchangeToReceive = currencyExchangeRates.get(i);
                if (!receiveEditText.getText().toString().isEmpty()) {
                    updateReceiverEditText();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mainLayout.setOnClickListener(view -> hideSoftKeyboard());
        submitButton.setOnClickListener(view -> submitButtonClicked());

        balanceTextView.setText(BalanceModel.getInstance().getBalanceToString());

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (firstLoad) {
                    Looper.prepare();
                    firstLoad = false;
                }
                getCurrencyExchangeRates(false);
            }
        }, 0, timeForRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrencyExchangeRates(true);
    }

    private void submitButtonClicked() {
        hideSoftKeyboard();
        if (validateInputForExchange()) {
            updateBalance();

            String message;
            if (BalanceModel.getInstance().getNoCommissionFeeExchanges() != 0) {
                message = String.format(getString(R.string.main_activity_successfully_exchanged_no_tax), sellEditText.getText().toString(), selectedCurrencyForExchangeToSell.getName(), receiveEditText.getText().toString(),
                        selectedCurrencyForExchangeToReceive.getName(), BalanceModel.getInstance().getNoCommissionFeeExchanges() - 1);
            } else {
                String fee = ExchangeCurrencyCalculator.calcCommissionFee(sellEditText.getText().toString());
                String feeFormat = String.format("%s %s", fee, selectedCurrencyForExchangeToSell.getName());

                message = String.format(getString(R.string.main_activity_successfully_exchanged_tax), sellEditText.getText().toString(), selectedCurrencyForExchangeToSell.getName(), receiveEditText.getText().toString(),
                        selectedCurrencyForExchangeToReceive.getName(), feeFormat);
            }
            showAlertDialog(message);
            BalanceModel.getInstance().decrementNoCommissionFeeParam();
            updateMainLayout();
        }
    }

    private void updateBalance() {
        String commissionFee = "";
        BalanceModel.getInstance().incrementNoCommissionFeeExchangeCounter();
        if (BalanceModel.getInstance().getNoCommissionFeeExchanges() == 0) {
            commissionFee = ExchangeCurrencyCalculator.calcCommissionFee(sellEditText.getText().toString());
        }

        BalanceModel.getInstance().updateBalance(selectedCurrencyForExchangeToSell.getName(), ExchangeCurrencyCalculator.subtractFromBalance(BalanceModel.getInstance().getSpecificAmountFromBalance(selectedCurrencyForExchangeToSell.getName()),
                sellEditText.getText().toString(), commissionFee));

        BalanceModel.getInstance().addCurrencyToBalance(new CurrencyOwnedModel(selectedCurrencyForExchangeToReceive.getName(), receiveEditText.getText().toString()));
        balanceTextView.setText(BalanceModel.getInstance().getBalanceToString());
    }

    private void updateMainLayout() {
        sellEditText.setText("");
        receiveEditText.setText("");
        receiveExchangeRateSpinner.setSelection(0);
        setUpSpinners(true);
    }

    private void updateReceiverEditText() {
        if (selectedCurrencyForExchangeToSell.getName().equals(selectedCurrencyForExchangeToReceive.getName())) {
            receiveEditText.setText(sellEditText.getText().toString());
        } else {
            receiveEditText.setText(ExchangeCurrencyCalculator.exchangeCurrency(selectedCurrencyForExchangeToSell.getName(), selectedCurrencyForExchangeToReceive.getName(),
                    sellEditText.getText().toString(), selectedCurrencyForExchangeToReceive.getExchangeRate(), getSpecificExchangeRate(selectedCurrencyForExchangeToSell.getName())));
        }
    }

    private void getCurrencyExchangeRates(boolean updateAllSpinners) {
        ServerCommunication.getInstance(this).getCurrencyExchangeRates(response -> {
            if (response.getCode().equals(ResponseModel.ResponseCode.CODE200)) {
                currencyExchangeRates = (ArrayList<CurrencyExchangeRateModel>) response.getData().get("serverData");
                setUpSpinners(updateAllSpinners);

            } else {
                showAlertDialogForError(getResources().getString(R.string.server_communication_default_error), null);
            }
        });
    }

    private void setUpSpinners(boolean updateAllSpinners) {
        if (updateAllSpinners) {
            ArrayList<String> ownedCurrencies = new ArrayList<>();
            for (CurrencyModel model : BalanceModel.getInstance().getBalance()) {
                ownedCurrencies.add(model.getName());
            }

            ArrayAdapter<String> sellSpinner = new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.spinnerTextView, ownedCurrencies);
            sellSpinner.setDropDownViewResource(R.layout.spinner_dropdown);
            sellExchangeRateSpinner.setAdapter(sellSpinner);
        }

        ArrayList<String> allCurrencies = new ArrayList<>();
        for (CurrencyExchangeRateModel model : currencyExchangeRates) {
            allCurrencies.add(model.getName());
        }

        ArrayAdapter<String> receiveSpinner = new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.spinnerTextView, allCurrencies);
        receiveSpinner.setDropDownViewResource(R.layout.spinner_dropdown);
        receiveExchangeRateSpinner.setAdapter(receiveSpinner);
    }

    private boolean validateInputForExchange() {
        if (sellEditText.getText().toString().isEmpty() || selectedCurrencyForExchangeToSell.getAmount().isEmpty()) {
            return false;
        }

        double balance = Double.parseDouble(selectedCurrencyForExchangeToSell.getAmount());
        double amount = Double.parseDouble(sellEditText.getText().toString());
        if (BalanceModel.getInstance().getNoCommissionFeeExchanges() == 0) {
            double commissionFee = Double.parseDouble(ExchangeCurrencyCalculator.calcCommissionFee(sellEditText.getText().toString()));
            amount = Double.sum(amount, commissionFee);
        }

        int result = Double.compare(balance, amount);
        if (result < 0) {
            showAlertDialogForError(getResources().getString(R.string.main_activity_successfully_exchanged_error_insufficient), null);
            return false;
        }

        return true;
    }

    public String getSpecificExchangeRate(String currencyName) {
        for (CurrencyExchangeRateModel model : this.currencyExchangeRates) {
            if (model.getName().equals(currencyName)) {
                return model.getExchangeRate();
            }
        }
        return "";
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.currency_converted);
        dialog.setMessage(message);
        dialog.setNeutralButton(R.string.done, null);
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
    }

    private void showAlertDialogForError(String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.warning);
        dialog.setMessage(message);
        dialog.setNeutralButton(R.string.close, listener);
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

}
