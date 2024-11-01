package com.jerry.myframwork;

import android.content.Intent;

import com.jerry.baselib.App;

import io.gate.gateapi.ApiClient;
import io.gate.gateapi.Configuration;

/**
 * Created by wzl on 2019/1/9.
 *
 * @Description
 */
public class MyApplication extends App {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, ListenerService.class));
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.gateio.ws/api/v4");
        defaultClient.setApiKeySecret("dd19b8507f51c7b666211941c7e2e6e3", "e5fa875a976e139ab4cea726459566e280e22ba6c80d4b46829377ce98cb61ab");
    }
}
