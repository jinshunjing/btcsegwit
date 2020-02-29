package org.jim.bitcoin.sign;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.jim.bitcoin.sign.model.BtcSig;
import org.jim.bitcoin.sign.model.BtcUtxo;
import org.jim.bitcoin.sign.model.BtcVinSig;
import org.jim.bitcoin.sign.model.BtcVout;
import org.jim.bitcoin.wallet.KeyBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sign MultiSig tx
 *
 * @author JSJ
 */
public class BtcMultiSigTxSigner {
    /**
     * Sign tx
     *
     * @param transaction
     * @param vins
     * @return
     */
    public static String signTransaction(Transaction transaction, List<BtcUtxo> vins) {
        KeyBag keyCache0 = KeyBag.getInstance();

        List<String> signatures = new ArrayList<>();
        int k = 0;
        for (BtcUtxo utxo : vins) {
            // get the private key
            String pubKey = keyCache0.getPubKey(utxo.getAddress());
            String redeemScript = keyCache0.getRedeemScript(utxo.getAddress());
            String privKey = keyCache0.getPrvKey(utxo.getAddress());

            // calculate the hash to be signed
            Sha256Hash hash = transaction.hashForSignature(k,
                    Utils.HEX.decode(redeemScript), Transaction.SigHash.ALL, false);

            // sign
            ECKey ecKey = ECKey.fromPrivate(Hex.decode(privKey));
            ECKey.ECDSASignature signature = ecKey.sign(hash);

            // generate the signature
            signatures.add(Utils.HEX.encode(signature.encodeToDER()) + ":" + pubKey);

            k++;
        }

        return StringUtils.join(signatures, ",");
    }

    /**
     * Build the tx
     *
     * @param network
     * @param vins
     * @param vouts
     * @param signatures
     * @return
     * @throws SignatureDecodeException
     */
    public static String buildTransaction(int network, List<BtcUtxo> vins, List<BtcVout> vouts,
                                          List<String> signatures) throws SignatureDecodeException {
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet3Params.get();
        Transaction transaction = new Transaction(networkParam);

        for (BtcUtxo utxo : vins) {
            TransactionOutPoint outPoint = new TransactionOutPoint(networkParam,
                    utxo.getN(), Sha256Hash.wrap(Utils.HEX.decode(utxo.getTxid())));
            TransactionInput vin = new TransactionInput(networkParam, transaction, new byte[0], outPoint);
            transaction.addInput(vin);

            // signatures has been sorted by public key
            List<TransactionSignature> sigs = new ArrayList<>();
            for (String sigHex : signatures) {
                TransactionSignature txSig = new TransactionSignature(
                        ECKey.ECDSASignature.decodeFromDER(Utils.HEX.decode(sigHex)),
                        Transaction.SigHash.ALL, false);
                sigs.add(txSig);
            }

            Script script = ScriptBuilder.createP2SHMultiSigInputScript(sigs,
                    new Script(Utils.HEX.decode(utxo.getRedeemScript())));
            vin.setScriptSig(script);
        }

        for (BtcVout vout : vouts) {
            transaction.addOutput(Coin.valueOf(vout.getAmount()), LegacyAddress.fromBase58(networkParam, vout.getAddress()));
        }

        String rawTx = Utils.HEX.encode(transaction.bitcoinSerialize());
        System.out.println(rawTx);
        return rawTx;
    }


    /**
     * 构造多重签名的交易
     *
     * @param vins 每个输入的赎回脚本，可能只有一个输入
     * @param signatures 签名人给出的签名与公钥，每个签名的长度是140个字符（70字节），以逗号分割
     * @return
     */
    public static String buildTransaction(Transaction transaction,
                                          List<BtcUtxo> vins,
                                          List<String> signatures) throws SignatureDecodeException {
        KeyBag keyCache0 = KeyBag.getInstance();

        // 准备每一个输入的签名
        List<BtcVinSig> sigList = new ArrayList<>();
        for (int k = 0; k < transaction.getInputs().size(); k++) {
            String addr = vins.get(k).getAddress();
            String pubKey = keyCache0.getPubKey(addr);
            String redeemScript = keyCache0.getRedeemScript(addr);
            String privKey = keyCache0.getPrvKey(addr);

            BtcVinSig txSig = new BtcVinSig();
            sigList.add(txSig);

            txSig.setRedeemScript(new Script(Utils.HEX.decode(redeemScript)));

            txSig.setSignatures(new ArrayList<>());
        }

        // 处理每一个签名人
        for (String signature : signatures) {
            String[] aList = signature.split(",");

            // 处理每一个输入
            int k = 0;
            for (String aStr : aList) {
                String[] bList = aStr.split(":");

                // 签名
                String sigHex = bList[0].trim();
                TransactionSignature txSig = new TransactionSignature(
                        ECKey.ECDSASignature.decodeFromDER(Utils.HEX.decode(sigHex)),
                        Transaction.SigHash.ALL, false);

                // 公钥
                String pubKeyHex = "00";
                if (1 < bList.length) {
                    pubKeyHex = bList[1].trim();
                }

                BtcSig aSig = new BtcSig();
                aSig.setSignature(txSig);
                aSig.setPubKey(pubKeyHex);

                sigList.get(k).getSignatures().add(aSig);
                k++;
            }
        }

        // 处理每一个输入
        int k = 0;
        for (TransactionInput vin : transaction.getInputs()) {
            BtcVinSig txSig = sigList.get(k);

            // FIXME: 签名需要按照公钥排序
            txSig.getSignatures().sort(Comparator.comparing(BtcSig::getPubKey));
            List<TransactionSignature> sigs = new ArrayList<>();
            for (BtcSig aSig : txSig.getSignatures()) {
                sigs.add(aSig.getSignature());
            }

            Script script = ScriptBuilder.createP2SHMultiSigInputScript(sigs, txSig.getRedeemScript());
            vin.setScriptSig(script);

            k++;
        }

        return Utils.HEX.encode(transaction.bitcoinSerialize());
    }
    /**
     * 解析已经部分签名的交易
     * 识别出输入的UTXO
     *
     * @param network
     * @param rawTx
     * @param utxoList
     * @param vins
     * @return
     */
    public static Transaction parseTransaction(int network, String rawTx,
                                               List<BtcUtxo> utxoList, List<BtcUtxo> vins) {
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet3Params.get();
        Transaction tx = new Transaction(networkParam, Utils.HEX.decode(rawTx));

        // 找出作为输入的UTXO
        for (TransactionInput vin : tx.getInputs()) {
            String txid = vin.getOutpoint().getHash().toString();
            int n = (int) vin.getOutpoint().getIndex();

            for (BtcUtxo utxo : utxoList) {
                if (utxo.getTxid().equals(txid) && utxo.getN() == n) {
                    vins.add(utxo);
                    break;
                }
            }
        }

        return tx;
    }

}
