package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet2Params;
import org.bitcoinj.script.ScriptBuilder;

/**
 * Calculate the hash of the segwit transaction
 */
public class SegwitTransactionHash {

    /**
     * Hash of P2SH(P2WPKH) transaction
     *
     * @param outpoint
     * @param amount
     * @param prevSequence
     * @param redeemScript
     * @param outputs
     * @param lockTime
     * @return
     */
    public static String hashForP2SHP2WPKH(String outpoint, String amount, String prevSequence,
                                    String redeemScript,  String outputs, String lockTime) {
        StringBuilder buffer = new StringBuilder();

        // version
        buffer.append("01000000");

        // hashPrevouts
        Sha256Hash hashPrevouts = Sha256Hash.twiceOf(Utils.HEX.decode(outpoint));
        buffer.append(hashPrevouts.toString());

        // hashSequence
        Sha256Hash hashSequence = Sha256Hash.twiceOf(Utils.HEX.decode(prevSequence));
        buffer.append(hashSequence.toString());

        // outpoint
        buffer.append(outpoint);

        // scriptCode
        buffer.append("1976a9").append(redeemScript.substring(2)).append("88ac");

        // amount
        buffer.append(amount);

        // nSequence
        buffer.append(prevSequence);

        // hashOutputs
        Sha256Hash hashOutputs = Sha256Hash.twiceOf(Utils.HEX.decode(outputs));
        buffer.append(hashOutputs.toString());

        // nLockTime
        buffer.append(lockTime);

        // nHashType
        buffer.append("01000000");

        Sha256Hash hash = Sha256Hash.twiceOf(Utils.HEX.decode(buffer.toString()));
        return hash.toString();
    }

    public static String hashForP2SHP2WSH(String outpoint, String amount, String prevSequence,
                                          String witnessScript,  String outputs, String lockTime) {
        StringBuilder buffer = new StringBuilder();

        // version
        buffer.append("01000000");

        // hashPrevouts
        Sha256Hash hashPrevouts = Sha256Hash.twiceOf(Utils.HEX.decode(outpoint));
        buffer.append(hashPrevouts.toString());

        // hashSequence
        Sha256Hash hashSequence = Sha256Hash.twiceOf(Utils.HEX.decode(prevSequence));
        buffer.append(hashSequence.toString());

        // outpoint
        buffer.append(outpoint);

        // scriptCode
        witnessScript = Utils.HEX.encode((new ScriptBuilder()).data(Utils.HEX.decode(witnessScript)).build().getProgram());
        buffer.append(witnessScript.substring(2));

        // amount
        buffer.append(amount);

        // nSequence
        buffer.append(prevSequence);

        // hashOutputs
        Sha256Hash hashOutputs = Sha256Hash.twiceOf(Utils.HEX.decode(outputs));
        buffer.append(hashOutputs.toString());

        // nLockTime
        buffer.append(lockTime);

        // nHashType
        buffer.append("01000000");

        System.out.println(buffer.toString());

        Sha256Hash hash = Sha256Hash.twiceOf(Utils.HEX.decode(buffer.toString()));
        return hash.toString();
    }

    /**
     * Create a new transaction
     *
     * @param vinTxid
     * @param vinIndex
     * @param outAddr
     * @param value
     * @return
     */
    public static String newTx(String vinTxid, long vinIndex, String outAddr, long value) {
        NetworkParameters networkParam = TestNet2Params.get();
        Transaction transaction = new Transaction(networkParam);

        // input
        TransactionOutPoint outPoint = new TransactionOutPoint(networkParam, vinIndex, Sha256Hash.wrap(Utils.HEX.decode(vinTxid)));
        TransactionInput vin = new TransactionInput(networkParam, transaction, new byte[0], outPoint);
        transaction.addInput(vin);

        // output
        transaction.addOutput(Coin.valueOf(value), Address.fromBase58(networkParam, outAddr));

        return Utils.HEX.encode(transaction.bitcoinSerialize());
    }

}
