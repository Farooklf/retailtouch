package com.lfsolutions.paymentslibrary.ascan;

import java.io.Serializable;


public class TransResponse implements Serializable{
    private String transaction_type;
    private String card_number;
    private String transaction_amount;
    private String date_time;
    private String expiry_date;
    private String entry_mode;
    private String retrieval_reference_number;
    private String approval_code;
    private String response_code;
    private String terminal_id;
    private String merchant_id;
    private String host_label;
    private String emv_data;
    private String card_label;
    private String card_type;
    private String host_type;
    private String command_identifier;
    private String custom_data_2;
    private String custom_data_3;
    private String ecr_unique_trace_number;
    private String invoice_number;
    private String transaction_info;
    private String batch_number;
    private String coupons_vouchers;
    private String additional_printing_flag;
    private String external_device_invoice;
    private String card_holder_name;
    private String employee_id;
    private String original_trans_type;

    public String getOriginal_trans_type() {
        return original_trans_type;
    }

    public void setOriginal_trans_type(String original_trans_type) {
        this.original_trans_type = original_trans_type;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(String transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getEntry_mode() {
        return entry_mode;
    }

    public void setEntry_mode(String entry_mode) {
        this.entry_mode = entry_mode;
    }

    public String getRetrieval_reference_number() {
        return retrieval_reference_number;
    }

    public void setRetrieval_reference_number(String retrieval_reference_number) {
        this.retrieval_reference_number = retrieval_reference_number;
    }

    public String getApproval_code() {
        return approval_code;
    }

    public void setApproval_code(String approval_code) {
        this.approval_code = approval_code;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getHost_label() {
        return host_label;
    }

    public void setHost_label(String host_label) {
        this.host_label = host_label;
    }

    public String getEmv_data() {
        return emv_data;
    }

    public void setEmv_data(String emv_data) {
        this.emv_data = emv_data;
    }

    public String getCard_label() {
        return card_label;
    }

    public void setCard_label(String card_label) {
        this.card_label = card_label;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getHost_type() {
        return host_type;
    }

    public void setHost_type(String host_type) {
        this.host_type = host_type;
    }

    public String getCommand_identifier() {
        return command_identifier;
    }

    public void setCommand_identifier(String command_identifier) {
        this.command_identifier = command_identifier;
    }

    public String getCustom_data_2() {
        return custom_data_2;
    }

    public void setCustom_data_2(String custom_data_2) {
        this.custom_data_2 = custom_data_2;
    }

    public String getCustom_data_3() {
        return custom_data_3;
    }

    public void setCustom_data_3(String custom_data_3) {
        this.custom_data_3 = custom_data_3;
    }

    public String getEcr_unique_trace_number() {
        return ecr_unique_trace_number;
    }

    public void setEcr_unique_trace_number(String ecr_unique_trace_number) {
        this.ecr_unique_trace_number = ecr_unique_trace_number;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getTransaction_info() {
        return transaction_info;
    }

    public void setTransaction_info(String transaction_info) {
        this.transaction_info = transaction_info;
    }

    public String getBatch_number() {
        return batch_number;
    }

    public void setBatch_number(String batch_number) {
        this.batch_number = batch_number;
    }

    public String getCoupons_vouchers() {
        return coupons_vouchers;
    }

    public void setCoupons_vouchers(String coupons_vouchers) {
        this.coupons_vouchers = coupons_vouchers;
    }

    public String getAdditional_printing_flag() {
        return additional_printing_flag;
    }

    public void setAdditional_printing_flag(String additional_printing_flag) {
        this.additional_printing_flag = additional_printing_flag;
    }

    public String getExternal_device_invoice() {
        return external_device_invoice;
    }

    public void setExternal_device_invoice(String external_device_invoice) {
        this.external_device_invoice = external_device_invoice;
    }

    public String getCard_holder_name() {
        return card_holder_name;
    }

    public void setCard_holder_name(String card_holder_name) {
        this.card_holder_name = card_holder_name;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }


}
