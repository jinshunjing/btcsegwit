package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet2Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

/**
 * Sign the tx with SegWit vin
 */
public class SignTransactionService {

    public static String signInput(int network, String rawTx, int vin, String prvKey, String scriptPubKey, String redeemScript) {
        // build tx
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet2Params.get();
        Transaction transaction = new Transaction(networkParam, Utils.HEX.decode(rawTx));

        // hash for sign
        Sha256Hash hash = transaction.hashForSignature(vin, Utils.HEX.decode(redeemScript), Transaction.SigHash.ALL, false);

        // sign
        ECKey ecKey = DumpedPrivateKey.fromBase58(networkParam, prvKey).getKey();
        ECKey.ECDSASignature signature = ecKey.sign(hash);
        String signatureHex = Utils.HEX.encode(signature.encodeToDER());

        return signatureHex;
    }

    public static String signInput(int network, String rawTx,
                              int vin, String prvKey,
                              String scriptPubKey, String redeemScript, String witnessScript) {
        // TransactionBuilder.prototype.addInput = function (txHash, vout, sequence, prevOutScript)
        // always provide prevOutScript

        // TransactionBuilder.prototype.sign = function (vin, keyPair, redeemScript, hashType, witnessValue, witnessScript)
        // P2PKH: (vin, keyPair, null, hashTye, null, null)
        // P2SH: (vin, keyPair, redeemScript, hashType, null, null)
        // P2SH(P2WPKH): (vin, keyPair, redeemScript, hashType, witnessValue, null)
        // P2SH(P2WSH): (vin, keyPair, redeemScript, hashType, witnessValue, witnessScript)

        // build tx
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet2Params.get();
        Transaction transaction = new Transaction(networkParam, Utils.HEX.decode(rawTx));

        // hash for sign
        Sha256Hash hash = transaction.hashForSignature(vin, Utils.HEX.decode(redeemScript), Transaction.SigHash.ALL, false);

        // sign
        ECKey ecKey = DumpedPrivateKey.fromBase58(networkParam, prvKey).getKey();
        ECKey.ECDSASignature signature = ecKey.sign(hash);
        String signatureHex = Utils.HEX.encode(signature.encodeToDER());

        // add into tx
        TransactionSignature txSig = new TransactionSignature(signature, Transaction.SigHash.ALL, false);
        Script scriptSig = ScriptBuilder.createInputScript(txSig);
        transaction.getInputs().get(0).setScriptSig(scriptSig);

        // serialize the tx
        String signedRawTx = Utils.HEX.encode(transaction.bitcoinSerialize());
        return signedRawTx;
    }

}
