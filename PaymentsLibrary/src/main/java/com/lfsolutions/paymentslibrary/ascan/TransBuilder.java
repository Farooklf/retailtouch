package com.lfsolutions.paymentslibrary.ascan;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransBuilder {
    public  static TransBuilder instance;
    public static TransBuilder getInstance(){
        if(instance ==null)
            instance = new TransBuilder();
        return instance;
    }
    public final DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00");


    //BUILD SALE OBJECT TO BE SENT TO PAYMENT APP
    public String getSaleObject(String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("transaction_type", GlobalConstants.ECR_SALE);
            jsonObject.put("retrieval_reference_number", "220117150420");
            jsonObject.put("command_identifier", getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }


    //BUILD SALE OBJECT TO BE SENT TO PAYMENT APP
    public String getEnquiryObject(String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", "0");
            jsonObject.put("transaction_type", GlobalConstants.ECR_ECHO);
            jsonObject.put("command_identifier", getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //BUILD VOID OBJECT TO BE SENT TO PAYMENT APP
    public String getSgVerifyObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_type", "C998");
            jsonObject.put("command_identifier", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String processJsonRequest = jsonObject.toString();
        return processJsonRequest;
    }


    //BUILD SALE OBJECT TO BE SENT TO PAYMENT APP
    public String getWalletSaleObject(String amount, String walletName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("transaction_type", GlobalConstants.ECR_WALLET_SALE);
            jsonObject.put("command_identifier", "1");
            jsonObject.put("custom_data_3", walletName); //ex: Dash, GrabPay,Alipay,Wechat

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //BUILD REFUND OBJECT TO BE SENT TO PAYMENT APP
    public String getRefundObject(String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("transaction_type", GlobalConstants.ECR_REFUND);
            jsonObject.put("command_identifier", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //BUILD SETTLEMENT OBJECT TO BE SENT TO PAYMENT APP
    public String getSettleObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_type", GlobalConstants.ECR_SETTLEMENT);
            jsonObject.put("command_identifier", "1");
            jsonObject.put("transaction_amount", "0");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    //BUILD SETTLEMENT OBJECT TO BE SENT TO PAYMENT APP
    public String getCardInfoObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_type", GlobalConstants.ECR_GET_CARD_DATA);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    //BUILD VOID OBJECT TO BE SENT TO PAYMENT APP
    public String getVoidObject(String invoiceNo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("invoice_number", invoiceNo);
            jsonObject.put("transaction_type", GlobalConstants.ECR_VOID);
            jsonObject.put("command_identifier", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //BUILD PREAUTH OBJECT TO BE SENT TO PAYMENT APP
    public String getPreauthObject(String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("transaction_type", "C201");
//            jsonObject.put("card_number", "5264711014071593");
//            jsonObject.put("expiry_date", "0425");
            jsonObject.put("command_identifier", getCurrentDate());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }


    //BUILD PREAUTH OBJECT TO BE SENT TO PAYMENT APP
    public String getPreauthManualCapObject(String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("transaction_type", "C604");
            jsonObject.put("retrieval_reference_number", "220117150420");

            //jsonObject.put("card_number", "#tICafsz72rawaz8K5M9SWRg7np0rvHx/Jl6E2gHGBmQ\\u003d"); //card number what you received in field 2
            //jsonObject.put("expiry_date", "#GkOuiIF3sm0Sg5xgn8G3XQ\\u003d\\u003d");//card expiry what you received in field 14
            //  jsonObject.put("approval_code", "872378");//Must be preauth approval code //field 38
            jsonObject.put("command_identifier", getCurrentDate());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }


    //BUILD OFFLINE OBJECT TO BE SENT TO PAYMENT APP
    public String getOfflineObject(String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("transaction_type", GlobalConstants.ECR_PRE_AUTH_CAPTURE);
            jsonObject.put("command_identifier", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //BUILD ECHO OBJECT TO BE SENT TO PAYMENT APP FOR GETTING THE STATUS
    public String getEchoObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_type", "C902");
            jsonObject.put("command_identifier", "1");
            jsonObject.put("transaction_amount", "0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }


    public String getManualPreAuthCompletion(String CardNo, String exp,String amount, String approvalCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("transaction_type", "C602");
            //#6ydtxwvs3Y7WmWSbmsS/DiCJKCmz/UQr8pbzC9Zs1Tc=
            jsonObject.put("card_number", CardNo);
            //#NxTep1ZmyTz2Qzz2FgavIg==
            jsonObject.put("expiry_date", exp);
            jsonObject.put("approval_code", approvalCode);
            jsonObject.put("command_identifier", getCurrentDate());
            jsonObject.put("command_identifier", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //BUILD TIP/ADJUST OBJECT TO BE SENT TO PAYMENT APP
    public String getTipObject(String invoiceNo, String tipAmount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("invoice_number", invoiceNo);
            jsonObject.put("transaction_amount", tipAmount);
            jsonObject.put("transaction_type", GlobalConstants.ECR_TIP_ADJUST);
            jsonObject.put("command_identifier", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //BUILD INQUIRY OBJECT TO BE SENT TO PAYMENT APP TO GET LAST TRANSACTION DETAILS
    public String getInquiryObject(String invoiceNo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_type", GlobalConstants.ECR_ECHO);
            //Pass your transaction command identifier
            jsonObject.put("command_identifier", "2021111523233");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }


    public String getConvertedAmount(String amount) {
        double l = Double.parseDouble(amount);
        // l = l / 100;
        return REAL_FORMATTER.format(l);

    }

    public static String getCurrentDate() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateToStr = format.format(today);
        return dateToStr;
    }

}