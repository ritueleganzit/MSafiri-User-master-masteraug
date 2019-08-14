/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.eleganz.msafiri.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created  on 5/28/2017.
 */

public class STKPush {

    @SerializedName("BusinessShortCode")
    public String businessShortCode;
    @SerializedName("Password")
    public String password;
    @SerializedName("Timestamp")
    public String timestamp;
    @SerializedName("TransactionType")
    public String transactionType;
    @SerializedName("Amount")
    public String amount;
    @SerializedName("PartyA")
    public String partyA;
    @SerializedName("PartyB")
    public String partyB;
    @SerializedName("PhoneNumber")
    public String phoneNumber;
    @SerializedName("CallBackURL")
    public String callBackURL;
    @SerializedName("AccountReference")
    public String accountReference;
    @SerializedName("TransactionDesc")
    public String transactionDesc;

    public STKPush(String businessShortCode, String password, String timestamp, String transactionType,
                   String amount, String partyA, String partyB, String phoneNumber, String callBackURL,
                   String accountReference, String transactionDesc) {
        this.businessShortCode = businessShortCode;
        this.password = password;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.amount = amount;
        this.partyA = partyA;
        this.partyB = partyB;
        this.phoneNumber = phoneNumber;
        this.callBackURL = callBackURL;
        this.accountReference = accountReference;
        this.transactionDesc = transactionDesc;
    }

    public String getBusinessShortCode() {
        return businessShortCode;
    }

    public String getPassword() {
        return password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getAmount() {
        return amount;
    }

    public String getPartyA() {
        return partyA;
    }

    public String getPartyB() {
        return partyB;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCallBackURL() {
        return callBackURL;
    }

    public String getAccountReference() {
        return accountReference;
    }

    public String getTransactionDesc() {
        return transactionDesc;
    }
}