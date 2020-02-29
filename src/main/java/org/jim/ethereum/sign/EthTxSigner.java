package org.jim.ethereum.sign;

import org.jim.ethereum.sign.model.EthSignature;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ETH tx signer
 *
 * @author JSJ
 */
public class EthTxSigner {
    /**
     * 创建普通转账交易
     *
     * @param chainId
     * @param _nonce
     * @param _gasPrice
     * @param _gasLimit
     * @param to
     * @param _value
     * @return
     */
    public static String createTransfer(byte chainId, long _nonce, long _gasPrice, long _gasLimit, String to, long _value) {
        BigInteger nonce = BigInteger.valueOf(_nonce);
        BigInteger gasPrice = new BigDecimal(_gasPrice).scaleByPowerOfTen(9).toBigInteger();
        BigInteger gasLimit = BigInteger.valueOf(_gasLimit);
        BigInteger value = new BigDecimal(_value).scaleByPowerOfTen(9).toBigInteger();

        RawTransaction transaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, value);
        byte[] payload = TransactionEncoder.encode(transaction, chainId);
        return Numeric.toHexString(payload);
    }

    /**
     * 创建合约调用
     *
     * @param chainId
     * @param _nonce
     * @param _gasPrice
     * @param _gasLimit
     * @param to
     * @param data
     * @return
     */
    public static String createCall(byte chainId, long _nonce, long _gasPrice, long _gasLimit, String to, String data) {
        BigInteger nonce = BigInteger.valueOf(_nonce);
        BigInteger gasPrice = new BigDecimal(_gasPrice).scaleByPowerOfTen(9).toBigInteger();
        BigInteger gasLimit = BigInteger.valueOf(_gasLimit);
        BigInteger value = BigInteger.ZERO;

        RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        byte[] payload = TransactionEncoder.encode(transaction, chainId);
        return Numeric.toHexString(payload);
    }

    /**
     * 构建签名后的交易
     *
     * @param rawTx
     * @param v
     * @param r
     * @param s
     * @return
     */
    public static String buildTransfer(byte chainId, String rawTx, String v, String r, String s) {
        Sign.SignatureData sig = new Sign.SignatureData(
                Numeric.toBigInt(v).byteValue(), Numeric.hexStringToByteArray(r), Numeric.hexStringToByteArray(s));
        byte[] payload = EthTxCodec.signMessage(chainId, rawTx, sig);
        return Numeric.toHexString(payload);
    }
    public static String buildTransfer(String rawTx, String v, String r, String s) {
        Sign.SignatureData sig = new Sign.SignatureData(
                Numeric.toBigInt(v).byteValue(), Numeric.hexStringToByteArray(r), Numeric.hexStringToByteArray(s));
        byte[] payload = EthTxCodec.signMessage(rawTx, sig);
        return Numeric.toHexString(payload);
    }

    /**
     * 签名
     *
     * @param rawTx
     * @param chainId
     * @param privKey
     * @return
     */
    public static EthSignature signTransfer(String rawTx, byte chainId, String privKey) {
        Credentials credentials = Credentials.create(privKey);

        byte[] payload = Numeric.hexStringToByteArray(rawTx);
        Sign.SignatureData signatureData = Sign.signMessage(payload, credentials.getEcKeyPair());
        Sign.SignatureData eip155SignatureData = TransactionEncoder.createEip155SignatureData(signatureData, chainId);

        EthSignature sig = new EthSignature();
        sig.setV(Numeric.toHexString(new byte[]{eip155SignatureData.getV()}));
        sig.setR(Numeric.toHexString(eip155SignatureData.getR()));
        sig.setS(Numeric.toHexString(eip155SignatureData.getS()));
        return sig;
    }

    /**
     * 转账ETH
     *
     * @param chainId
     * @param privKey
     * @param _nonce
     * @param _gasPrice
     * @param _gasLimit
     * @param to
     * @param _value
     * @return
     */
    public static String transferEther(byte chainId, String privKey,
                                       long _nonce, long _gasPrice, long _gasLimit, String to, long _value) {
        BigInteger nonce = BigInteger.valueOf(_nonce);
        BigInteger gasPrice = new BigDecimal(_gasPrice).scaleByPowerOfTen(9).toBigInteger();
        BigInteger gasLimit = BigInteger.valueOf(_gasLimit);
        BigInteger value = new BigDecimal(_value).scaleByPowerOfTen(9).toBigInteger();

        Credentials credentials = Credentials.create(privKey);

        RawTransaction transaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, value);

        byte[] rawTx = TransactionEncoder.signMessage(transaction, chainId, credentials);
        return Numeric.toHexString(rawTx);
    }

}
