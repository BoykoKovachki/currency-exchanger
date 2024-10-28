package com.example.currencyexchanger.utils;

import android.util.Log;

import com.example.currencyexchanger.models.CurrencyExchangeRateModel;
import com.example.currencyexchanger.models.ResponseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Parser {

    public static ResponseModel parserForExchangeRates(byte[] bytes) {
        ResponseModel response = new ResponseModel();

        try {
            JSONObject jsonObject = new JSONObject(new String(bytes));
            JSONObject jsonObjectRates = jsonObject.getJSONObject("rates");

            ArrayList<CurrencyExchangeRateModel> exchangeRates = new ArrayList<>();

            JSONArray jsonArray = jsonObjectRates.toJSONArray(jsonObjectRates.names());
            Iterator<String> x = jsonObjectRates.keys();

            int i = 0;
            while (x.hasNext()) {
                CurrencyExchangeRateModel exchangeRate = new CurrencyExchangeRateModel();
                exchangeRate.setName((String) x.next());
                exchangeRate.setExchangeRate(String.valueOf(jsonArray.get(i++)));

                exchangeRates.add(exchangeRate);
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("serverData", exchangeRates);

            response.setData(data);
            response.setCode(ResponseModel.ResponseCode.CODE200);

        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e("ERROR: ", e.getMessage());
            }
            response.setCode(ResponseModel.ResponseCode.CODE1103);
        }

        return response;
    }
}
