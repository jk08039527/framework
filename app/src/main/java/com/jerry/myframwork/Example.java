package com.jerry.myframwork;

import java.util.List;

import io.gate.gateapi.ApiClient;
import io.gate.gateapi.ApiException;
import io.gate.gateapi.Configuration;
import io.gate.gateapi.api.SpotApi;
import io.gate.gateapi.models.CurrencyPair;

public class Example {

    public static void main(String[] args) {
       try {
            SpotApi spotApi = new SpotApi();
            List<CurrencyPair> list = spotApi.listCurrencyPairs();
            System.out.println(list);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}