package com.bupocket.wallet;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.common.Constants;
import com.bupocket.http.api.AccountService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DecimalCalculate;
import com.bupocket.utils.LogUtils;
import com.bupocket.wallet.enums.BUChainExceptionEnum;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletBPData;
import com.bupocket.wallet.utils.KeyStore;
import com.bupocket.wallet.utils.keystore.BaseKeyStoreEntity;
import com.bupocket.wallet.utils.keystore.KeyStoreEntity;

import io.bumo.SDK;
import io.bumo.common.ToBaseUnit;
import io.bumo.encryption.crypto.mnemonic.Mnemonic;
import io.bumo.encryption.key.PrivateKey;
import io.bumo.encryption.utils.hex.HexFormat;
import io.bumo.model.request.*;
import io.bumo.model.request.operation.*;
import io.bumo.model.response.*;
import io.bumo.model.response.result.ContractCallResult;
import io.bumo.model.response.result.TransactionBuildBlobResult;

import org.bitcoinj.crypto.MnemonicCode;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private static SDK sdk = null;
    private static Wallet wallet;

    private Wallet() {
        init();
    }

    private void init() {
        sdk = SDK.getInstance(Constants.BUMO_NODE_URL_BASE);
    }

    public synchronized static Wallet getInstance() {
        if (wallet == null) {
            wallet = new Wallet();
        }
        return wallet;
    }

    public void setNull4Wallet() {
        wallet = null;
    }

    private WalletBPData createIdentity(String password, String sKey, Context context) throws WalletException {
        try {
            WalletBPData walletBPData;
            List<String> mnemonicCodes;
            BaseKeyStoreEntity baseKeyStoreEntity = KeyStore.encryptMsg(password, sKey, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 1);
            List<String> hdPaths = new ArrayList<>();
            hdPaths.add("M/44H/526H/0H/0/0");
            hdPaths.add("M/44H/526H/1H/0/0");
            mnemonicCodes = new MnemonicCode().toMnemonic(HexFormat.hexStringToBytes(sKey));
            List<String> privateKeys = Mnemonic.generatePrivateKeys(mnemonicCodes, hdPaths);


            walletBPData = new WalletBPData();
            walletBPData.setSkey(JSON.toJSONString(baseKeyStoreEntity));
            List<WalletBPData.AccountsBean> accountsBeans = new ArrayList<>();

            KeyStoreEntity keyStoreEntity = null;
            WalletBPData.AccountsBean accountsBean;
            for (String pk : privateKeys) {
                keyStoreEntity = KeyStore.generateKeyStore(password, pk, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 1);
                accountsBean = new WalletBPData.AccountsBean();
                accountsBean.setAddress(new PrivateKey(pk).getEncAddress());
                accountsBean.setSecret(JSON.toJSONString(keyStoreEntity));
                accountsBeans.add(accountsBean);
            }
            walletBPData.setAccounts(accountsBeans);
            walletBPData.setMnemonicCodes(mnemonicCodes);
            WalletBPData.AccountsBean identityAccountBean = accountsBeans.get(0);
            WalletBPData.AccountsBean walletAccountBean = accountsBeans.get(1);

            String walletAccountPk = getPk(privateKeys.get(1));

            String walletAccountSignData = signData(privateKeys.get(1), walletAccountBean.getAddress());

            deviceBind(walletAccountBean.getAddress(), identityAccountBean.getAddress(), walletAccountPk, walletAccountSignData, context);

            return walletBPData;

        } catch (Exception e) {
            e.printStackTrace();
            throw new WalletException(ExceptionEnum.SYS_ERR.getCode(), ExceptionEnum.SYS_ERR.getMessage());
        }
    }

    private WalletBPData createWallet(String password, String sKey, Context context) throws WalletException {
        try {
            WalletBPData walletBPData;
            List<String> mnemonicCodes;
            BaseKeyStoreEntity baseKeyStoreEntity = KeyStore.encryptMsg(password, sKey, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 1);
            List<String> hdPaths = new ArrayList<>();
            hdPaths.add("M/44H/526H/1H/0/0");
            mnemonicCodes = new MnemonicCode().toMnemonic(HexFormat.hexStringToBytes(sKey));
            List<String> privateKeys = Mnemonic.generatePrivateKeys(mnemonicCodes, hdPaths);


            walletBPData = new WalletBPData();
            walletBPData.setSkey(JSON.toJSONString(baseKeyStoreEntity));
            List<WalletBPData.AccountsBean> accountsBeans = new ArrayList<>();

            KeyStoreEntity keyStoreEntity = null;
            WalletBPData.AccountsBean accountsBean;
            for (String pk : privateKeys) {
                keyStoreEntity = KeyStore.generateKeyStore(password, pk, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 1);
                accountsBean = new WalletBPData.AccountsBean();
                accountsBean.setAddress(new PrivateKey(pk).getEncAddress());
                accountsBean.setSecret(JSON.toJSONString(keyStoreEntity));
                accountsBeans.add(accountsBean);
            }
            walletBPData.setAccounts(accountsBeans);
            walletBPData.setMnemonicCodes(mnemonicCodes);
            WalletBPData.AccountsBean identityAccountBean = accountsBeans.get(0);
            WalletBPData.AccountsBean walletAccountBean = accountsBeans.get(0);

            String walletAccountPk = getPk(privateKeys.get(0));

            String walletAccountSignData = signData(privateKeys.get(0), walletAccountBean.getAddress());

            deviceBind(walletAccountBean.getAddress(), identityAccountBean.getAddress(), walletAccountPk, walletAccountSignData, context);

            return walletBPData;

        } catch (Exception e) {
            e.printStackTrace();
            throw new WalletException(ExceptionEnum.SYS_ERR.getCode(), ExceptionEnum.SYS_ERR.getMessage());
        }
    }

    public WalletBPData createIdentity(String password, Context context) throws WalletException {
        byte[] aesIv = new byte[16];
        SecureRandom randomIv = new SecureRandom();
        randomIv.nextBytes(aesIv);
        String skey = HexFormat.byteToHex(aesIv);
        return createIdentity(password, skey, context);
    }

    public WalletBPData createWallet(String password, Context context) throws WalletException {
        byte[] aesIv = new byte[16];
        SecureRandom randomIv = new SecureRandom();
        randomIv.nextBytes(aesIv);
        String skey = HexFormat.byteToHex(aesIv);
        return createWallet(password, skey, context);
    }


    public WalletBPData updateAccountPassword(String oblPwd, String newPwd, String ciphertextSkeyData, Context context) throws WalletException {
        try {
            // 校验密码是否匹配
            String sKey = HexFormat.byteToHex(getSkey(oblPwd, ciphertextSkeyData));
            return createIdentity(newPwd, sKey, context);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WalletException(ExceptionEnum.SYS_ERR.getCode(), ExceptionEnum.SYS_ERR.getMessage());
        }
    }


    public WalletBPData updateAccountWalletPassword(String oblPwd, String newPwd, String privData, Context context) throws WalletException {
        try {

            BaseKeyStoreEntity baseKeyStoreEntity = JSON.parseObject(privData, BaseKeyStoreEntity.class);
            String decodePrivData = KeyStore.decodeMsg(oblPwd, baseKeyStoreEntity);
            return importPrivateKey(newPwd, decodePrivData);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WalletException(ExceptionEnum.SYS_ERR.getCode(), ExceptionEnum.SYS_ERR.getMessage());
        }
    }


    public WalletBPData importMnemonicCode(List<String> mnemonicCodes, String password, Context context) throws Exception {
        byte[] sKeyByte = new MnemonicCode().toEntropy(mnemonicCodes);
        String sKey = HexFormat.byteToHex(sKeyByte);
        return createIdentity(password, sKey, context);
    }

    public void checkPwd(String password, String ciphertextSkeyData) throws Exception {
        getSkey(password, ciphertextSkeyData);
    }

    public static byte[] getSkey(String password, String ciphertextSkeyData) throws Exception {
        BaseKeyStoreEntity baseKeyStoreEntity = JSON.parseObject(ciphertextSkeyData, BaseKeyStoreEntity.class);
        String skeyHex = KeyStore.decodeMsg(password, baseKeyStoreEntity);
        return HexFormat.hexToByte(skeyHex);
    }


    public String getAccountBUBalance(String accountAddress) {
        System.out.print(Constants.BUMO_NODE_URL);
        AccountGetBalanceRequest request = new AccountGetBalanceRequest();
        request.setAddress(accountAddress);
        AccountGetBalanceResponse response = sdk.getAccountService().getBalance(request);

        System.out.println(JSON.toJSONString(response, true));
        LogUtils.e("getAccountBUBalance:" + response.getErrorCode() + "\t" + response.getErrorDesc());
        if (0 == response.getErrorCode()) {
            return ToBaseUnit.MO2BU(response.getResult().getBalance().toString());
        }
        return null;
    }

    public Boolean checkAccAddress(String address) {
        Boolean flag = false;
        AccountCheckValidRequest accountCheckValidRequest = new AccountCheckValidRequest();
        accountCheckValidRequest.setAddress(address);
        AccountCheckValidResponse accountCheckValidResponse = sdk.getAccountService().checkValid(accountCheckValidRequest);
        if (0 == accountCheckValidResponse.getErrorCode()) {
            return accountCheckValidResponse.getResult().isValid();
        } else {
            System.out.println(JSON.toJSONString(accountCheckValidResponse, true));
        }
        return flag;
    }


    public AccountGetInfoResponse GetInfo(String address) throws WalletException {
        Boolean flag = false;

        AccountGetInfoRequest accountGetInfoRequest = new AccountGetInfoRequest();
        accountGetInfoRequest.setAddress(address);
        AccountGetInfoResponse info = sdk.getAccountService().getInfo(accountGetInfoRequest);
        if (0 == info.getErrorCode()) {

        } else {
            throw new WalletException(ExceptionEnum.ADDRESS_NOT_EXIST.getCode(), ExceptionEnum.ADDRESS_NOT_EXIST.getMessage());
        }
//        } else if (ExceptionEnum.ADDRESS_NOT_EXIST.getCode().equals(info.getErrorCode() + "")) {
//            throw new WalletException(ExceptionEnum.ADDRESS_NOT_EXIST.getCode(), ExceptionEnum.ADDRESS_NOT_EXIST.getMessage());
//        } else {
//            System.out.println(JSON.toJSONString(accountGetInfoRequest, true));
//        }
        return info;
    }


    public long checkToAddressValidateAndOpenAccount(String password, String bPData, String fromAddr, String toAddr, String amount, String fee) {
        long nonce = 0;
        try {
            AccountGetInfoResponse accountGetInfoResponse = Wallet.getInstance().GetInfo(toAddr);
        } catch (WalletException e) {
            e.printStackTrace();
            //address not exist
            try {
                nonce = Wallet.getInstance().getAccountNonce(fromAddr) + 1;
                String hash = Wallet.getInstance().sendBuNoNonceVoucher(password, bPData, fromAddr, toAddr, amount, "", fee, nonce);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return nonce;
    }


    public String sendBuNoNonce(String password, String bPData, String fromAccAddr, String toAccAddr, String amount, String note, String fee, long nonce) throws Exception {
        String hash = null;
        try {
            String senderPrivateKey = getPKBYAccountPassword(password, bPData, fromAccAddr);
            // Init variable
            // The account address to receive bu
            String destAddress = toAccAddr;
            // The amount to be sent
            Long sendAmount = ToBaseUnit.BU2MO(amount);
            // The fixed write 1000L, the unit is MO
            Long gasPrice = 1000L;
            // Set up the maximum cost 0.01BU
            Long feeLimit = ToBaseUnit.BU2MO(fee);
            // Transaction initiation account's nonce + 1
            String transMetadata = note;

//            Long nonce = getAccountNonce(fromAccAddr) + 1;
            hash = sendBu(senderPrivateKey, destAddress, sendAmount, nonce, gasPrice, feeLimit, transMetadata);
        } catch (WalletException e) {
            throw new WalletException(e.getErrCode(), e.getErrMsg());
        } catch (Exception e) {
            throw new Exception(e);
        }
        return hash;
    }

    public String sendBuNoNonceVoucher(String senderPrivateKey, String bPData, String fromAccAddr, String toAccAddr, String amount, String note, String fee, long nonce) throws Exception {
        String hash = null;
        try {
            // Init variable
            // The account address to receive bu
            String destAddress = toAccAddr;
            // The amount to be sent
            Long sendAmount = ToBaseUnit.BU2MO(amount);
            // The fixed write 1000L, the unit is MO
            Long gasPrice = 1000L;
            // Set up the maximum cost 0.01BU
            Long feeLimit = ToBaseUnit.BU2MO(fee);
            // Transaction initiation account's nonce + 1
            String transMetadata = note;

//            Long nonce = getAccountNonce(fromAccAddr) + 1;
            hash = sendBu(senderPrivateKey, destAddress, sendAmount, nonce, gasPrice, feeLimit, transMetadata);
        } catch (WalletException e) {
            throw new WalletException(e.getErrCode(), e.getErrMsg());
        } catch (Exception e) {
            throw new Exception(e);
        }
        return hash;
    }


    public String sendBu(String password, String bPData, String fromAccAddr, String toAccAddr, String amount, String note, String fee) throws Exception {
        String hash = null;
        try {
            String senderPrivateKey = getPKBYAccountPassword(password, bPData, fromAccAddr);
            // Init variable
            // The account address to receive bu
            String destAddress = toAccAddr;
            // The amount to be sent
            Long sendAmount = ToBaseUnit.BU2MO(amount);
            // The fixed write 1000L, the unit is MO
            Long gasPrice = 1000L;
            // Set up the maximum cost 0.01BU
            Long feeLimit = ToBaseUnit.BU2MO(fee);
            // Transaction initiation account's nonce + 1
            String transMetadata = note;

            Long nonce = getAccountNonce(fromAccAddr) + 1;
            hash = sendBu(senderPrivateKey, destAddress, sendAmount, nonce, gasPrice, feeLimit, transMetadata);
        } catch (WalletException e) {
            throw new WalletException(e.getErrCode(), e.getErrMsg());
        } catch (Exception e) {
            throw new Exception(e);
        }
        return hash;
    }

    private String sendBu(String senderPrivateKey, String destAddress, Long amount, Long senderNonce, Long gasPrice, Long feeLimit, String transMetadata) throws Exception {

        String senderAddresss = getAddressByPrivateKey(senderPrivateKey);
        BUSendOperation buSendOperation = new BUSendOperation();
        buSendOperation.setSourceAddress(senderAddresss);
        buSendOperation.setDestAddress(destAddress);
        buSendOperation.setAmount(amount);

        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(senderAddresss);
        transactionBuildBlobRequest.setNonce(senderNonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        transactionBuildBlobRequest.addOperation(buSendOperation);
        transactionBuildBlobRequest.setMetadata(transMetadata);

        String transactionBlob = null;
        TransactionBuildBlobResponse transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
        TransactionBuildBlobResult transactionBuildBlobResult = transactionBuildBlobResponse.getResult();
        String txHash = transactionBuildBlobResult.getHash();
        transactionBlob = transactionBuildBlobResult.getTransactionBlob();

        // 5. Sign transaction BLob
        String[] signerPrivateKeyArr = {senderPrivateKey};
        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
        transactionSignRequest.setBlob(transactionBlob);
        for (int i = 0; i < signerPrivateKeyArr.length; i++) {
            transactionSignRequest.addPrivateKey(signerPrivateKeyArr[i]);
        }
        TransactionSignResponse transactionSignResponse = sdk.getTransactionService().sign(transactionSignRequest);

        // 6. Broadcast transaction
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(transactionSignResponse.getResult().getSignatures());
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        if (0 == transactionSubmitResponse.getErrorCode()) {
            System.out.println("Success，hash=" + transactionSubmitResponse.getResult().getHash());
        } else {
            if (BUChainExceptionEnum.ERRCODE_FEE_NOT_ENOUGH.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.FEE_NOT_ENOUGH);
            } else if (BUChainExceptionEnum.ERRCODE_ACCOUNT_LOW_RESERVE.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.BU_NOT_ENOUGH);
            }
            System.out.println("Failure，hash=" + transactionSubmitResponse.getResult().getHash() + "");
            System.out.println(JSON.toJSONString(transactionSubmitResponse, true));
        }
        return txHash;
    }


    public String sendTokenNoNonce(String password, String bPData, String fromAccAddr, String toAccAddr, String tokenCode, String tokenIssuer, String amount, String decimals, String note, String fee, long nonce) throws Exception {
        String hash = null;
        try {
            String senderPrivateKey = getPKBYAccountPassword(password, bPData, fromAccAddr);
            String metadata = note;
            // The fixed write 1000L, the unit is MO
            Long gasPrice = 1000L;


            // handle send token amount

            Long sendTokenAmount = handleSendTokenAmount(amount, decimals);

            // Transaction initiation account's Nonce + 1
//            String senderAddress = getAddressByPrivateKey(senderPrivateKey);
//            Long nonce = getAccountNonce(senderAddress) + 1;


            List<BaseOperation> operations = new ArrayList<>();


            String senderAddresss = getAddressByPrivateKey(senderPrivateKey);

            // Check whether the destination account is activated
            if (!checkAccountActivated(toAccAddr)) {
                if (Double.parseDouble(fee) - Constants.ACTIVE_AMOUNT_FEE <= 0) {
                    throw new WalletException(ExceptionEnum.FEE_NOT_ENOUGH);
                }
                AccountActivateOperation accountActivateOperation = new AccountActivateOperation();
                accountActivateOperation.setSourceAddress(senderAddresss);
                accountActivateOperation.setDestAddress(toAccAddr);
                accountActivateOperation.setInitBalance(ToBaseUnit.BU2MO(Constants.ACTIVE_AMOUNT_FEE + ""));
                accountActivateOperation.setMetadata("activate account");
                operations.add(accountActivateOperation);
                fee = DecimalCalculate.sub(Double.parseDouble(fee), Constants.ACTIVE_AMOUNT_FEE) + "";
            }

            // Set up the maximum cost 0.01BU
            Long feeLimit = ToBaseUnit.BU2MO(fee);

            // Build asset operation
            AssetSendOperation assetSendOperation = new AssetSendOperation();
            assetSendOperation.setSourceAddress(senderAddresss);
            assetSendOperation.setDestAddress(toAccAddr);
            assetSendOperation.setCode(tokenCode);
            assetSendOperation.setAmount(sendTokenAmount);
            assetSendOperation.setIssuer(tokenIssuer);
            assetSendOperation.setMetadata(metadata);

            operations.add(assetSendOperation);

//        BaseOperation[] operations = {operation};
            // Record txhash for subsequent confirmation of the real result of the transaction.
            // After recommending five blocks, call again through txhash `Get the transaction information
            // from the transaction Hash'(see example: getTxByHash ()) to confirm the final result of the transaction
            hash = submitTransaction(senderPrivateKey, senderAddresss, operations, nonce, gasPrice, feeLimit, note);
        } catch (WalletException e) {
            throw new WalletException(e.getErrCode(), e.getErrMsg());
        } catch (Exception e) {
            throw new Exception(e);
        }
        return hash;
    }

    public String sendToken(String password, String bPData, String fromAccAddr, String toAccAddr, String tokenCode, String tokenIssuer, String sendTokenAamount, String tokenDecimals, String note, String fee) throws Exception {
        String hash = null;
        try {
            String senderPrivateKey = getPKBYAccountPassword(password, bPData, fromAccAddr);
            hash = sendToken(senderPrivateKey, tokenIssuer, toAccAddr, tokenCode, sendTokenAamount, tokenDecimals, note, fee);

        } catch (WalletException e) {
            throw new WalletException(e.getErrCode(), e.getErrMsg());
        } catch (Exception e) {
            throw new Exception(e);
        }
        return hash;
    }

    public String getPKBYAccountPassword(String password, String bPData, String fromAccAddr) throws Exception {
        String senderPrivateKey = null;
        List<WalletBPData.AccountsBean> accountsBeans = JSON.parseArray(bPData, WalletBPData.AccountsBean.class);

        if (accountsBeans.size() > 0) {
            for (WalletBPData.AccountsBean accountsBean : accountsBeans
            ) {
                if (fromAccAddr.equals(accountsBean.getAddress())) {
                    senderPrivateKey = KeyStore.decodeMsg(password, JSON.parseObject(accountsBean.getSecret().toString(), BaseKeyStoreEntity.class));
                    if (!senderPrivateKey.startsWith("priv")) {
                        throw new Exception();
                    }
                    break;
                }
            }
        }
        return senderPrivateKey;
    }


    private String sendToken(String senderPrivateKey, String issuerAddress, String destAddress, String code, String amount, String decimals, String note, String fee) throws Exception {
        // The operation notes
        String metadata = note;
        // The fixed write 1000L, the unit is MO
        Long gasPrice = 1000L;


        // handle send token amount

        Long sendTokenAmount = handleSendTokenAmount(amount, decimals);

        // Transaction initiation account's Nonce + 1
        String senderAddress = getAddressByPrivateKey(senderPrivateKey);
        Long nonce = getAccountNonce(senderAddress) + 1;


        List<BaseOperation> operations = new ArrayList<>();


        String senderAddresss = getAddressByPrivateKey(senderPrivateKey);

        // Check whether the destination account is activated
        if (!checkAccountActivated(destAddress)) {
            if (Double.parseDouble(fee) - Constants.ACTIVE_AMOUNT_FEE <= 0) {
                throw new WalletException(ExceptionEnum.FEE_NOT_ENOUGH);
            }
            AccountActivateOperation accountActivateOperation = new AccountActivateOperation();
            accountActivateOperation.setSourceAddress(senderAddresss);
            accountActivateOperation.setDestAddress(destAddress);
            accountActivateOperation.setInitBalance(ToBaseUnit.BU2MO(Constants.ACTIVE_AMOUNT_FEE + ""));
            accountActivateOperation.setMetadata("activate account");
            operations.add(accountActivateOperation);
            fee = DecimalCalculate.sub(Double.parseDouble(fee), Constants.ACTIVE_AMOUNT_FEE) + "";
        }

        // Set up the maximum cost 0.01BU
        Long feeLimit = ToBaseUnit.BU2MO(fee);

        // Build asset operation
        AssetSendOperation assetSendOperation = new AssetSendOperation();
        assetSendOperation.setSourceAddress(senderAddresss);
        assetSendOperation.setDestAddress(destAddress);
        assetSendOperation.setCode(code);
        assetSendOperation.setAmount(sendTokenAmount);
        assetSendOperation.setIssuer(issuerAddress);
        assetSendOperation.setMetadata(metadata);

        operations.add(assetSendOperation);

//        BaseOperation[] operations = {operation};
        // Record txhash for subsequent confirmation of the real result of the transaction.
        // After recommending five blocks, call again through txhash `Get the transaction information
        // from the transaction Hash'(see example: getTxByHash ()) to confirm the final result of the transaction
        String txHash = submitTransaction(senderPrivateKey, senderAddresss, operations, nonce, gasPrice, feeLimit, note);
        return txHash;
    }


    private Long handleSendTokenAmount(String srcAmount, String decimals) {
        return Long.parseLong(new BigDecimal(srcAmount).multiply(new BigDecimal(Math.pow(10, Integer.parseInt(decimals)))).setScale(0).toPlainString());
    }

    private String submitTransaction(String senderPrivateKey, String senderAddresss, List<BaseOperation> operations, Long senderNonce, Long gasPrice, Long feeLimit, String transMetadata) throws Exception {
        // 1. Build transaction
        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(senderAddresss);
        transactionBuildBlobRequest.setNonce(senderNonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        transactionBuildBlobRequest.setMetadata(transMetadata);
        for (int i = 0; i < operations.size(); i++) {
            transactionBuildBlobRequest.addOperation(operations.get(i));
        }

        transactionBuildBlobRequest.setMetadata(transMetadata);

        // 2. Build transaction BLob
        String transactionBlob;
        TransactionBuildBlobResponse transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
        if (transactionBuildBlobResponse.getErrorCode() != 0) {
            System.out.println("error: " + transactionBuildBlobResponse.getErrorDesc());
            return null;
        }
        TransactionBuildBlobResult transactionBuildBlobResult = transactionBuildBlobResponse.getResult();
        transactionBlob = transactionBuildBlobResult.getTransactionBlob();

        // 3. Sign transaction BLob
        String[] signerPrivateKeyArr = {senderPrivateKey};
        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
        transactionSignRequest.setBlob(transactionBlob);
        for (int i = 0; i < signerPrivateKeyArr.length; i++) {
            transactionSignRequest.addPrivateKey(signerPrivateKeyArr[i]);
        }
        TransactionSignResponse transactionSignResponse = sdk.getTransactionService().sign(transactionSignRequest);
        if (transactionSignResponse.getErrorCode() != 0) {
            System.out.println("error: " + transactionSignResponse.getErrorDesc());
            return null;
        }

        // 4. Broadcast transaction
        String Hash = null;
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(transactionSignResponse.getResult().getSignatures());
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        if (0 == transactionSubmitResponse.getErrorCode()) {
            Hash = transactionSubmitResponse.getResult().getHash();
        } else {
            if (BUChainExceptionEnum.ERRCODE_FEE_NOT_ENOUGH.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.FEE_NOT_ENOUGH);
            } else if (BUChainExceptionEnum.ERRCODE_ACCOUNT_LOW_RESERVE.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.BU_NOT_ENOUGH);
            }
            System.out.println(JSON.toJSONString(transactionSubmitResponse, true));
        }
        return Hash;
    }

    /**
     * Check whether an account is activated.\
     */
    public boolean checkAccountActivated(String address) {
        AccountCheckActivatedRequst request = new AccountCheckActivatedRequst();
        request.setAddress(address);

        AccountCheckActivatedResponse response = sdk.getAccountService().checkActivated(request);
        if (response.getErrorCode() != 0) {
            return false;
        }
        System.out.println(response.getResult().getIsActivated());
        return response.getResult().getIsActivated();
    }

    private String getAddressByPrivateKey(String privatekey) throws Exception {
        String publicKey = PrivateKey.getEncPublicKey(privatekey);
        String address = PrivateKey.getEncAddress(publicKey);
        return address;
    }

    public Long getAccountNonce(String accountAddress) throws WalletException {
        AccountGetNonceRequest request = new AccountGetNonceRequest();
        request.setAddress(accountAddress);

        AccountGetNonceResponse response = sdk.getAccountService().getNonce(request);
        if (0 == response.getErrorCode()) {
            return response.getResult().getNonce();
        } else if (11007 == response.getErrorCode()) {
            throw new WalletException(response.getErrorCode().toString(), response.getErrorDesc());
        } else if (20000 == response.getErrorCode()) {
            throw new WalletException(response.getErrorCode().toString(), response.getErrorDesc());
        } else if (4 == response.getErrorCode()) {
            throw new WalletException(response.getErrorCode().toString(), response.getErrorDesc());
        }
        return null;
    }

    private void deviceBind(final String walletAddress, final String identityAddress, final String publicKey, final String signData, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                AccountService accountService = RetrofitFactory.getInstance().getRetrofit().create(AccountService.class);
                Map<String, Object> parmasMap = new HashMap<>();
                parmasMap.put("walletAddress", walletAddress);
                parmasMap.put("identityAddress", identityAddress);
                parmasMap.put("deviceId", CommonUtil.getUniqueId(context));
                parmasMap.put("publicKey", publicKey);
                parmasMap.put("signData", signData);

                Call<ApiResult> call = accountService.deviceBind(parmasMap);
                call.enqueue(new Callback<ApiResult>() {
                    @Override
                    public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                        ApiResult respDto = response.body();
                    }

                    @Override
                    public void onFailure(Call<ApiResult> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }).start();
    }


    private String getPk(String sk) {
        return PrivateKey.getEncPublicKey(sk);
    }


    private String signData(String sk, String message) {
        return HexFormat.byteToHex(PrivateKey.sign(message.getBytes(), sk));
    }

    private Long handleTokenAmount(String srcAmount, String decimals) {
        return Long.parseLong(new BigDecimal(srcAmount).multiply(new BigDecimal(Math.pow(10, Double.parseDouble(decimals)))).setScale(0).toPlainString());
    }

    /**
     * issue asset
     */
    public String issueAtp10Token(String password, String bPData, String fromAccAddr, String code, String decimals, String issueAmount, String fee) throws Exception {
        // The account private key to issue atp1.0 token
        String issuerPrivateKey = getPKBYAccountPassword(password, bPData, fromAccAddr);
        // The token now supply number
        Long nowSupply = handleTokenAmount(issueAmount, decimals);
        ;
        // The operation note
        String operationMetadata = "";
        // The transaction note
        String transMetadata = "";
        // Transaction initiation account's Nonce + 1
        Long nonce = getAccountNonce(fromAccAddr) + 1;
        // The fixed write 1000L, the unit is MO
        Long gasPrice = 1000L;
        // Set up the maximum cost 0.01BU
        Long feeLimit = ToBaseUnit.BU2MO(fee);

        // 1. Get the account address to send this transaction
        String issuerAddresss = getAddressByPrivateKey(issuerPrivateKey);

        // 2. Build asset operation
        AssetIssueOperation assetIssueOperation = new AssetIssueOperation();
        assetIssueOperation.setSourceAddress(issuerAddresss);
        assetIssueOperation.setCode(code);
        assetIssueOperation.setAmount(nowSupply);
        assetIssueOperation.setMetadata(operationMetadata);

        List<BaseOperation> operations = new ArrayList<>();
        operations.add(assetIssueOperation);
        // Record txhash for subsequent confirmation of the real result of the transaction.
        // After recommending five blocks, call again through txhash `Get the transaction information
        // from the transaction Hash'(see example: getTxByHash ()) to confirm the final result of the transaction
        String txHash = submitTransaction(issuerPrivateKey, issuerAddresss, operations, nonce, gasPrice, feeLimit, transMetadata);
        if (txHash != null) {
            System.out.println("hash: " + txHash);
        }
        return txHash;
    }


    public String registerATP10Token(String password, String bPData, String fromAccAddr, String name, String code, String decimals, String description, String fee, String tokenAmount) throws Exception {
        // The account private key to issue atp1.0 token
        String issuerPrivateKey = getPKBYAccountPassword(password, bPData, fromAccAddr);
        // The apt token version
        String version = "1.0";
        // The apt token icon
        String icon = "";
        // The token total supply number
        Long totalSupply = handleTokenAmount(tokenAmount, decimals);
        // The operation note
        String operationMetadata = "";
        // The transaction note
        String transMetadata = "";
        // Transaction initiation account's Nonce + 1
        Long nonce = getAccountNonce(fromAccAddr) + 1;
        // The fixed write 1000L, the unit is MO
        Long gasPrice = 1000L;
        // Set up the maximum cost 0.01BU
        Long feeLimit = ToBaseUnit.BU2MO(fee);

        JSONObject atp10Json = new JSONObject();
        atp10Json.put("name", name);
        atp10Json.put("code", code);
        atp10Json.put("description", description);
        atp10Json.put("decimals", Integer.valueOf(decimals));
        atp10Json.put("totalSupply", totalSupply);
        atp10Json.put("icon", icon);
        atp10Json.put("version", version);
//        atp10Json.put("tokenType",tokenType);

        String key = "asset_property_" + code;
        String value = atp10Json.toJSONString();

        AccountSetMetadataOperation accountSetMetadataOperation = new AccountSetMetadataOperation();
        accountSetMetadataOperation.setSourceAddress(fromAccAddr);
        accountSetMetadataOperation.setKey(key);
        accountSetMetadataOperation.setValue(value);
        accountSetMetadataOperation.setMetadata(operationMetadata);
        List<BaseOperation> operations = new ArrayList<>();
        operations.add(accountSetMetadataOperation);

        String txHash = submitTransaction(issuerPrivateKey, fromAccAddr, operations, nonce, gasPrice, feeLimit, transMetadata);
        return txHash;
    }

    public String exportKeyStore(String password, String accountBPData, String walletPublicAddress) throws Exception {
        String senderPrivateKey = null;
        String keystore = null;
        List<WalletBPData.AccountsBean> accountsBeans = JSON.parseArray(accountBPData, WalletBPData.AccountsBean.class);

        if (accountsBeans.size() > 0) {
            for (WalletBPData.AccountsBean accountsBean : accountsBeans) {
                if (walletPublicAddress.equals(accountsBean.getAddress())) {
                    senderPrivateKey = KeyStore.decodeMsg(password, JSON.parseObject(accountsBean.getSecret().toString(), BaseKeyStoreEntity.class));
                    if (!senderPrivateKey.startsWith("priv")) {
                        throw new Exception();
                    }
                    keystore = JSON.toJSONString(KeyStore.generateKeyStore(password, senderPrivateKey, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 2));
                    break;
                }
            }
        }
        return keystore;
    }

    public String exportPrivateKey(String password, String accountBPData, String walletPublicAddress) throws Exception {
        String senderPrivateKey = null;
        List<WalletBPData.AccountsBean> accountsBeans = JSON.parseArray(accountBPData, WalletBPData.AccountsBean.class);

        if (accountsBeans.size() > 0) {
            for (WalletBPData.AccountsBean accountsBean : accountsBeans) {
                if (walletPublicAddress.equals(accountsBean.getAddress())) {
                    senderPrivateKey = KeyStore.decodeMsg(password, JSON.parseObject(accountsBean.getSecret().toString(), BaseKeyStoreEntity.class));
                    if (!senderPrivateKey.startsWith("priv")) {
                        throw new Exception();
                    }
                    break;
                }
            }
        }
        return senderPrivateKey;
    }

    public WalletBPData importKeystore(String password, String keystore) throws Exception {
        String privateKey = KeyStore.decipherKeyStore(password, JSON.parseObject(keystore, KeyStoreEntity.class));
        if (!privateKey.startsWith("priv")) {
            throw new Exception();
        }
        String address = new PrivateKey(privateKey).getEncAddress();
        KeyStoreEntity keyStoreEntity = KeyStore.generateKeyStore(password, privateKey, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 2);
        WalletBPData walletBPData = new WalletBPData();
        List<WalletBPData.AccountsBean> accountsBeans = new ArrayList<>();
        WalletBPData.AccountsBean accountsBean = new WalletBPData.AccountsBean();
        accountsBean.setAddress(address);
        accountsBean.setSecret(JSON.toJSONString(keyStoreEntity));
        accountsBeans.add(accountsBean);
        walletBPData.setAccounts(accountsBeans);
        walletBPData.setSkey(JSON.toJSONString(keyStoreEntity));
        return walletBPData;
    }

    public WalletBPData importPrivateKey(String password, String privateKey) throws Exception {
        String address = new PrivateKey(privateKey).getEncAddress();
        KeyStoreEntity keyStoreEntity = KeyStore.generateKeyStore(password, privateKey, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 2);
        WalletBPData walletBPData = new WalletBPData();
        List<WalletBPData.AccountsBean> accountsBeans = new ArrayList<>();
        WalletBPData.AccountsBean accountsBean = new WalletBPData.AccountsBean();
        accountsBean.setAddress(address);
        accountsBean.setSecret(JSON.toJSONString(keyStoreEntity));
        accountsBeans.add(accountsBean);
        walletBPData.setAccounts(accountsBeans);
        walletBPData.setSkey(JSON.toJSONString(keyStoreEntity));
        return walletBPData;
    }

    /**
     * build blob
     *
     * @param buAmount
     * @param input
     * @param fromAccAddr
     * @param nonce
     * @param feeLimit
     * @param gasPrice
     * @return
     */

    private TransactionBuildBlobResponse buildBlob(Long buAmount, JSONObject input, String fromAccAddr, Long nonce, Long feeLimit, Long gasPrice, String contractAddress) {
        // build contractInvokeByBUOperation
        ContractInvokeByBUOperation contractInvokeByBUOperation = new ContractInvokeByBUOperation();
        contractInvokeByBUOperation.setContractAddress(contractAddress);
        contractInvokeByBUOperation.setBuAmount(buAmount);
        contractInvokeByBUOperation.setInput(input.toJSONString());

        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(fromAccAddr);
        transactionBuildBlobRequest.setNonce(nonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        transactionBuildBlobRequest.addOperation(contractInvokeByBUOperation);

        String transactionBlob = null;
        TransactionBuildBlobResponse transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
        return transactionBuildBlobResponse;
    }

    public TransactionBuildBlobResponse buildBlob(String amount, String input, String sourceAddress, String fee, String contractAddress, String transMetadata) throws Exception {

        TransactionBuildBlobResponse transactionBuildBlobResponse = null;
        try {
            Long nonce = getAccountNonce(sourceAddress) + 1;
            Long gasPrice = 1000L;
            Long feeLimit = ToBaseUnit.BU2MO(fee);
            Long buAmount = ToBaseUnit.BU2MO(amount);

            ContractInvokeByBUOperation contractInvokeByBUOperation = new ContractInvokeByBUOperation();
            contractInvokeByBUOperation.setContractAddress(contractAddress);
            contractInvokeByBUOperation.setBuAmount(buAmount);
            contractInvokeByBUOperation.setInput(input);

            if (!transMetadata.isEmpty()) {
                contractInvokeByBUOperation.setMetadata(transMetadata);
            }


            TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
            transactionBuildBlobRequest.setSourceAddress(sourceAddress);
            transactionBuildBlobRequest.setNonce(nonce);
            transactionBuildBlobRequest.setFeeLimit(feeLimit);
            transactionBuildBlobRequest.setGasPrice(gasPrice);
            transactionBuildBlobRequest.addOperation(contractInvokeByBUOperation);
            if (!transMetadata.isEmpty()) {
                transactionBuildBlobRequest.setMetadata(transMetadata);
            }

            transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
            LogUtils.e("transactionBuildBlobResponse:" + transactionBuildBlobResponse.getErrorCode() + "" +
                    "t" + transactionBuildBlobResponse.getErrorDesc());
        } catch (WalletException response) {
            throw new WalletException(response.getErrCode(), response.getErrMsg());
        }
        return transactionBuildBlobResponse;
    }

    public TransactionBuildBlobResponse applyCoBuildBlob(String sourceAddress, String amount, String initInput, String payload, double fee, String transMetaData) throws Exception {
        Long initBalance = ToBaseUnit.BU2MO(amount);
        // The fixed write 1000L ，the unit is MO
        Long gasPrice = 1000L;
        // Set up the maximum cost 10.01BU
        Long feeLimit = ToBaseUnit.BU2MO(fee + "");//ToBaseUnit.BU2MO("10.01");
        // Transaction initiation account's Nonce + 1
        Long nonce = getAccountNonce(sourceAddress) + 1;

        // 2. Build activateAccount operation
        ContractCreateOperation operation = new ContractCreateOperation();
        operation.setSourceAddress(sourceAddress);
        operation.setInitBalance(initBalance);
        operation.setPayload(payload);
        operation.setInitInput(initInput);
        operation.setMetadata(transMetaData);

        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(sourceAddress);
        transactionBuildBlobRequest.setNonce(nonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        transactionBuildBlobRequest.addOperation(operation);
        transactionBuildBlobRequest.setMetadata(transMetaData);

        TransactionBuildBlobResponse transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
        LogUtils.e("transactionBuildBlobResponse:" + transactionBuildBlobResponse.getErrorCode() + "" +
                "t" + transactionBuildBlobResponse.getErrorDesc());
        return transactionBuildBlobResponse;
    }


    public String submitTransaction(String senderPrivateKey, TransactionBuildBlobResponse transactionBuildBlobResponse) throws Exception {
        String transactionBlob = transactionBuildBlobResponse.getResult().getTransactionBlob();

        String[] signerPrivateKeyArr = {senderPrivateKey};
        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
        transactionSignRequest.setBlob(transactionBlob);
        for (int i = 0; i < signerPrivateKeyArr.length; i++) {
            transactionSignRequest.addPrivateKey(signerPrivateKeyArr[i]);
        }
        TransactionSignResponse transactionSignResponse = sdk.getTransactionService().sign(transactionSignRequest);

        // 6. Broadcast transaction
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(transactionSignResponse.getResult().getSignatures());
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        String txHash = null;
        if (0 == transactionSubmitResponse.getErrorCode()) {
            txHash = transactionSubmitResponse.getResult().getHash();
            LogUtils.e("Success，hash=" + transactionSubmitResponse.getResult().getHash());
            System.out.println("Success，hash=" + transactionSubmitResponse.getResult().getHash());
        } else {
            if (BUChainExceptionEnum.ERRCODE_FEE_NOT_ENOUGH.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.FEE_NOT_ENOUGH);
            } else if (BUChainExceptionEnum.ERRCODE_ACCOUNT_LOW_RESERVE.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.BU_NOT_ENOUGH);
            } else if (BUChainExceptionEnum.ERRCODE_OTHER.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.SUBMIT_TRANSACTION_ERROR);
            }

//          errorCode  152
            LogUtils.e("\nFailure，code=" + transactionSubmitResponse.getErrorCode() + transactionSubmitResponse.getErrorDesc() + "\n，hash=" + transactionSubmitResponse.getResult().getHash() + "");
            System.out.println("Failure，hash=" + transactionSubmitResponse.getResult().getHash() + "");
            System.out.println(JSON.toJSONString(transactionSubmitResponse, true));
        }
        return txHash;
    }


    public String submitTransaction(String password, String bPData, String sourceAddress, TransactionBuildBlobResponse transactionBuildBlobResponse) throws Exception {
        String transactionBlob = transactionBuildBlobResponse.getResult().getTransactionBlob();
        String senderPrivateKey;
        try {
            senderPrivateKey = getPKBYAccountPassword(password, bPData, sourceAddress);
        } catch (Exception e) {
            throw new WalletException(ExceptionEnum.PASSWORD_ERROR.getCode(), ExceptionEnum.PASSWORD_ERROR.getMessage());
        }


        String[] signerPrivateKeyArr = {senderPrivateKey};
        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
        transactionSignRequest.setBlob(transactionBlob);
        for (int i = 0; i < signerPrivateKeyArr.length; i++) {
            transactionSignRequest.addPrivateKey(signerPrivateKeyArr[i]);
        }
        TransactionSignResponse transactionSignResponse = sdk.getTransactionService().sign(transactionSignRequest);

        // 6. Broadcast transaction
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(transactionSignResponse.getResult().getSignatures());
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        String txHash = null;
        if (0 == transactionSubmitResponse.getErrorCode()) {
            txHash = transactionSubmitResponse.getResult().getHash();
            LogUtils.e("Success，hash=" + transactionSubmitResponse.getResult().getHash());
            System.out.println("Success，hash=" + transactionSubmitResponse.getResult().getHash());
        } else {
            if (BUChainExceptionEnum.ERRCODE_FEE_NOT_ENOUGH.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.FEE_NOT_ENOUGH);
            } else if (BUChainExceptionEnum.ERRCODE_ACCOUNT_LOW_RESERVE.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.BU_NOT_ENOUGH);
            } else if (BUChainExceptionEnum.ERRCODE_OTHER.getCode().equals(transactionSubmitResponse.getErrorCode())) {
                throw new WalletException(ExceptionEnum.SUBMIT_TRANSACTION_ERROR);
            }

//          errorCode  152
            LogUtils.e("\nFailure，code=" + transactionSubmitResponse.getErrorCode() + transactionSubmitResponse.getErrorDesc() + "\n，hash=" + transactionSubmitResponse.getResult().getHash() + "");
            System.out.println("Failure，hash=" + transactionSubmitResponse.getResult().getHash() + "");
            System.out.println(JSON.toJSONString(transactionSubmitResponse, true));
        }
        return txHash;
    }


    public ContractCallResult callContract(String contractAddress, String input, double fee) {

        // Amount
        Long feeLimit = ToBaseUnit.BU2MO(fee + "");


        // Init request
        ContractCallRequest request = new ContractCallRequest();
        request.setContractAddress(contractAddress);
        request.setFeeLimit(feeLimit);
        request.setOptType(2);
        request.setInput(input);

        // Call call
        ContractCallResponse response = sdk.getContractService().call(request);
        if (response.getErrorCode() == 0) {
            ContractCallResult result = response.getResult();
            return result;
        } else {
            System.out.println("error: " + response.getErrorDesc());
        }

        return null;
    }
}



