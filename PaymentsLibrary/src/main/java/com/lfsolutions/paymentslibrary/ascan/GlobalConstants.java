package com.lfsolutions.paymentslibrary.ascan;


/**
 * Created by ascanmac on 26/5/18.
 */

public class GlobalConstants {


    public static final String ECR_SALE = "C200";
    public static final String ECR_PE_SALE = "C600";

    public static final String ECR_PRE_AUTH = "C201";
    public static final String ECR_PRE_AUTH_CAPTURE = "C202";
    public static final String ECR_PRE_AUTH_ME_CAPTURE = "C602";

    public static final String ECR_REFUND = "C203";
    public static final String ECR_QUASI_CASH_ADVANCE = "C204";
    public static final String ECR_GET_CARD_DATA = "C902";
    public static final String ECR_VOID = "C300";
    public static final String ECR_PRE_AUTH_CANCEL = "C301"; // 2017032101 Rajesh: Added PRE AUTH CANCEL DEV TMS-13131313
    public static final String ECR_INQUIRY = "C400";
    public static final String ECR_ECHO = "C902";
    public static final String ECR_BEGIN_SHIFT = "C800";
    public static final String ECR_GET_MAGSTRIPE = "C810";
    public static final String ECR_TIP_ADJUST = "C500";
    public static final String ECR_CASH_ADVANCE = "C205";
    public static final String ECR_SETTLEMENT = "C700";
    public static final String ECR_WAIT = "C920";
    public static final String ECR_RETRIEVE_TERMINAL_INFO = "C900"; // Retrieve terminal info ie. TID, Serial Number Etc..
    public static final String ECR_EZLINK_SALE = "C610";//Ezlink sale
    public static final String ECR_CUSTOM_RECEIPT = "C620";//Ezlink Blacklist Download
    public static final String ECR_WALLET_SALE = "C640";//Ezlink Blacklist Download
    public static final String PACKAGE_NAME_DBS = "sg.com.eftpos.mobilepos.dbs"; //Enable this for DBS bank
    public static final String PACKAGE_NAME_OCBC = "sg.com.eftpos.mobilepos.ocbc"; //Enable this for Ocbc bank
    public static final String PACKAGE_NAME_BOC = "sg.com.eftpos.mobilepos.boc"; //Enable this for Boc bank
    public static final String ASCAN_CLASS_NAME = "sg.com.mobileeftpos.paymentapplication.ecr.EcrGatewayActivity";


}
