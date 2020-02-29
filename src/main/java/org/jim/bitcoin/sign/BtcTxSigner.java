package org.jim.bitcoin.sign;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.jim.bitcoin.sign.model.BtcUtxo;
import org.jim.bitcoin.sign.model.BtcVout;
import org.jim.bitcoin.wallet.KeyBag;

import java.util.List;

/**
 * Sign tx
 *
 * @author JSJ
 */
public class BtcTxSigner {
    /**
     * Create and sing a raw transaction
     *
     * @param network
     * @param vins
     * @param vouts
     * @return
     */
    public static String signTx(int network, List<BtcUtxo> vins, List<BtcVout> vouts) {
        Transaction tx = createRawTx(network, vins, vouts);
        return signInputs(tx, vins);
    }

    /**
     * Create a raw transaction
     *
     * @param network
     * @return
     */
    public static Transaction createRawTx(int network, List<BtcUtxo> vins, List<BtcVout> vouts) {
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet3Params.get();
        Transaction transaction = new Transaction(networkParam);

        for (BtcUtxo utxo : vins) {
            TransactionOutPoint outPoint = new TransactionOutPoint(networkParam,
                    utxo.getN(), Sha256Hash.wrap(Utils.HEX.decode(utxo.getTxid())));
            TransactionInput vin = new TransactionInput(networkParam, transaction, new byte[0], outPoint);
            transaction.addInput(vin);
        }

        for (BtcVout vout : vouts) {
            transaction.addOutput(Coin.valueOf(vout.getAmount()), LegacyAddress.fromBase58(networkParam, vout.getAddress()));
        }

        return transaction;
    }

    /**
     * Sign the transaction inputs
     *
     * @param transaction
     * @param vins
     * @return
     */
    public static String signInputs(Transaction transaction, List<BtcUtxo> vins) {
        KeyBag keyCache0 = KeyBag.getInstance();

        int k = 0;
        for (BtcUtxo utxo : vins) {
            // get private key
            String privKey = keyCache0.getPrvKey(utxo.getAddress());

            // calculate the hash to be signed
            Sha256Hash hash = transaction.hashForSignature(k,
                    Utils.HEX.decode(utxo.getRedeemScript()), Transaction.SigHash.ALL, false);

            // sign
            ECKey ecKey = ECKey.fromPrivate(Hex.decode(privKey));
            ECKey.ECDSASignature signature = ecKey.sign(hash);

            // generate the sig script
            TransactionSignature txSig = new TransactionSignature(signature.r, signature.s);
            Script script = ScriptBuilder.createInputScript(txSig, ecKey);

            transaction.getInput(k).setScriptSig(script);

            k++;
        }

        return Utils.HEX.encode(transaction.bitcoinSerialize());
    }

}
