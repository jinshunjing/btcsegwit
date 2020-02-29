package org.jim.ethereum.sign;

import org.apache.commons.lang3.StringUtils;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.SignedRawTransaction;
import org.web3j.rlp.*;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * ETH Codec: fix bugs of web3j
 *
 * @author JSJ
 */
public class EthTxCodec {
    /**
     * 给交易加上签名
     *
     * @param chainId
     * @param signatureData
     * @return
     */
    public static byte[] signMessage(byte chainId, String rawTx, Sign.SignatureData signatureData) {
        RawTransaction rawTransaction = decode(rawTx);
        byte v = (byte)(signatureData.getV() + (chainId << 1) + 8);
        Sign.SignatureData eip155SignatureData = new Sign.SignatureData(v, signatureData.getR(), signatureData.getS());
        List<RlpType> values = encode(rawTransaction, eip155SignatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }
    public static byte[] signMessage(String rawTx, Sign.SignatureData signatureData) {
        RawTransaction rawTransaction = decode(rawTx);
        List<RlpType> values = encode(rawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    /**
     * 编码原始交易
     *
     * @param rawTransaction
     * @param signatureData
     * @return
     */
    public static List<RlpType> encode(RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> result = new ArrayList();
        result.add(RlpString.create(rawTransaction.getNonce()));
        result.add(RlpString.create(rawTransaction.getGasPrice()));
        result.add(RlpString.create(rawTransaction.getGasLimit()));
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 0) {
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }
        result.add(RlpString.create(rawTransaction.getValue()));
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));

        // 签名
        if (signatureData != null) {
            result.add(RlpString.create(signatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
        }

        return result;
    }

    /**
     * 解码原始交易
     *
     * @param hexTransaction
     * @return
     */
    public static RawTransaction decode(String hexTransaction) {
        byte[] transaction = Numeric.hexStringToByteArray(hexTransaction);
        RlpList rlpList = RlpDecoder.decode(transaction);
        RlpList values = (RlpList)rlpList.getValues().get(0);

        BigInteger nonce = resolveBigInteger(values,0);
        BigInteger gasPrice = resolveBigInteger(values,1);
        BigInteger gasLimit = resolveBigInteger(values,2);

        String to = ((RlpString)values.getValues().get(3)).asString();

        BigInteger value = resolveBigInteger(values, 4);

        String data = ((RlpString)values.getValues().get(5)).asString();

        // 签名
        if (values.getValues().size() > 6) {
            byte v = ((RlpString)values.getValues().get(6)).getBytes()[0];
            byte[] r = Numeric.toBytesPadded(Numeric.toBigInt(((RlpString)values.getValues().get(7)).getBytes()), 32);
            byte[] s = Numeric.toBytesPadded(Numeric.toBigInt(((RlpString)values.getValues().get(8)).getBytes()), 32);
            Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);
            return new SignedRawTransaction(nonce, gasPrice, gasLimit, to, value, data, signatureData);
        } else {
            return RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        }
    }

    /**
     * 解决负数的bug
     *
     * @param values
     * @return
     */
    private static BigInteger resolveBigInteger(RlpList values, int idx) {
        BigInteger value = ((RlpString)values.getValues().get(idx)).asBigInteger();
        if (value.compareTo(BigInteger.ZERO) < 0) {
            String val = ((RlpString) values.getValues().get(idx)).asString();
            if (StringUtils.isNotEmpty(val)) {
                value = Numeric.toBigInt(val);
            }
        }
        return value;
    }

}
