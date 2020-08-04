package com.ajdeveloper.instadownloader.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import com.ajdeveloper.instadownloader.R;

public class IAPHelper {
    private Context context;
    private static final String LOG_TAG = "iabv3";
    // PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "videodownloader.hdvideos.downloadvideos.purchased";
    private static final String LICENSE_KEY = String.valueOf(R.string.iap_license_key);
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    public IAPHelper(Context context) {
        this.context = context;
        handleIAP();
        registerBC();
    }

    private void handleIAP() {
        if (!BillingProcessor.isIabServiceAvailable(context)) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(context, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {

            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                setsharedPrefrence();
                showLog("onProductPurchased: " + productId);

            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                showLog("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
                showLog("onBillingInitialized");
//                bp.consumePurchase(PRODUCT_ID);
                readyToPurchase = true;
                for (String sku : bp.listOwnedProducts()) {
                    if (sku.equals(PRODUCT_ID)) {
                        showLog("Owned Managed Product: " + sku);
                        setsharedPrefrence();
                    }
                }
            }

            @Override
            public void onPurchaseHistoryRestored() {
                for (String sku : bp.listOwnedProducts()) {
                    if (sku.equals(PRODUCT_ID)) {
                        showLog("Owned Managed Product: " + sku);
                        setsharedPrefrence();
                    }
                }
            }

        });

    }

    public BillingProcessor getBp() {
        return bp;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void showLog(String message) {
        Log.e(LOG_TAG, message);
    }

    public void showDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        //builder.setTitle("Turn Off Gallery Duplication")
        builder.setTitle(Html.fromHtml("<font color='#E73902'>Purchase App</font>"))
                .setMessage("Ads will be removed by purchasing app.")
                .setPositiveButton("Purchase", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!readyToPurchase) {
                            showToast("Billing not initialized.");
                            return;
                        }
                        if (bp.isOneTimePurchaseSupported()) {
                            bp.purchase((Activity) context, PRODUCT_ID);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog bui = builder.create();
        bui.show();
    }

    private void setsharedPrefrence() {
        SharedPreferences inAppPurchase = context.getSharedPreferences("check_Purchased", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = inAppPurchase.edit();
        editor.putString("Value", "Purchased");
        editor.apply();
        sendBC();
    }

    private void setsharedPrefrence_ForCheck() {
        SharedPreferences inAppPurchase = context.getSharedPreferences("check_Purchased", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = inAppPurchase.edit();
        editor.putString("Value", "hello");
        editor.apply();
        sendBC();
    }

    private void sendBC() {
        Log.e("BC_", "SENT");
        context.sendBroadcast(new Intent("xcoders.instasaver.purchasing_check"));
    }

    private void registerBC() {
        if (context != null)
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    showDialog();
                }
            }, new IntentFilter("xcoders.instasaver.IAP_ACTION"));
    }

}