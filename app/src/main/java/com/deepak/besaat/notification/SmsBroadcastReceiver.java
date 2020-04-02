package com.deepak.besaat.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SmsBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
      Bundle extras = intent.getExtras();
      Status mStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
 
      switch (mStatus.getStatusCode()) {
        case CommonStatusCodes.SUCCESS:
          String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
            String otp = message.replace("<#> Your otp code is : ", "");
          break;
        case CommonStatusCodes.TIMEOUT:
          // Waiting for SMS timed out (5 minutes)
           Log.e("sms failure","true");
          break;
      }
    }
  }
}