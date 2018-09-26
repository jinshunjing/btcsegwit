package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test P2SH(P2WPKH)
 */
@RunWith(SpringRunner.class)
public class P2shP2wpkhTest {


    /**
     * Create a simple transaction
     */
    @Test
    public void testNewTx() {
        String txid = "58f76ed4c099b7ed14ea64ac099e31483862c457dac187d85135f25dc66ed43d";
        long vout = 0L;

        String addr = "mrwFaerzWhX4W8g5gL2LrjHJzZYdxkw85D";
        long value = 4800000L;

        String rawTx = SegwitTransactionHash.newTx(txid, vout, addr, value);
        System.out.println(rawTx);
        // 01000000013dd46ec65df23551d887c1da57c4623848319e09ac64ea14edb799c0d46ef7580000000000ffffffff01003e4900000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac00000000
    }

    /**
     * Get the input signature
     */
    @Test
    public void testInputSignature() {
        String outpint = "3dd46ec65df23551d887c1da57c4623848319e09ac64ea14edb799c0d46ef75800000000";
        String amount = "404b4c0000000000";
        String prevSequence = "ffffffff";

        String outputs = "003e4900000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac";
        String lockTime = "00000000";

        String redeemScript = "001450507ef7413040b843344d32f74927fcb2fc74a8";
        String prvKey = "L1e26WVtSgYxxXrSBKTZqeJN7d9Tna8r3LUhki6csStu5zCagE1R";

        String txHash = SegwitTransactionHash.hashForP2SHP2WPKH(outpint, amount, prevSequence, redeemScript, outputs, lockTime);

        String signature = SignTransactionService.signInputByWIF(txHash, prvKey);
        System.out.println(signature);
        // 3045022100ecb5ceacc3452b07884abfdb34e24d65a1ea5097515c7f585c21fa7bf24f92f502203cbb060c4434386ad098c65ca658f3efbbae1efc3a2891ba1320416792974028
    }

    /**
     * Get the signed transaction
     */
    @Test
    public void testSignTx() {
        String rawTx = "01000000013dd46ec65df23551d887c1da57c4623848319e09ac64ea14edb799c0d46ef7580000000000ffffffff01003e4900000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac00000000";
        String signature = "3045022100ecb5ceacc3452b07884abfdb34e24d65a1ea5097515c7f585c21fa7bf24f92f502203cbb060c4434386ad098c65ca658f3efbbae1efc3a2891ba132041679297402801";

        String pubKey = "0274f7bcdf9b6e2d3aa0cded33871f4e4a1956b1c3214b5c65dc7da61306d35d4e";
        String redeemScript = "001450507ef7413040b843344d32f74927fcb2fc74a8";

        // parse unsigned tx
        SegwitTransaction transaction = new SegwitTransaction(MainNetParams.get(), Utils.HEX.decode(rawTx));

        // add redeem script
        Script inputScript = (new ScriptBuilder()).data(Utils.HEX.decode(redeemScript)).build();
        transaction.getInput(0).setScriptSig(inputScript);

        // add witness script
        TransactionWitness txWitness = new TransactionWitness();
        txWitness.addPush(Utils.HEX.decode(signature));
        txWitness.addPush(Utils.HEX.decode(pubKey));
        transaction.setWitness(0, txWitness);

        // generate the signed tx
        String signedRawTx = Utils.HEX.encode(transaction.bitcoinSerialize());
        System.out.println(signedRawTx);
        // 010000000001013dd46ec65df23551d887c1da57c4623848319e09ac64ea14edb799c0d46ef758000000001716001450507ef7413040b843344d32f74927fcb2fc74a8ffffffff01003e4900000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac02483045022100ecb5ceacc3452b07884abfdb34e24d65a1ea5097515c7f585c21fa7bf24f92f502203cbb060c4434386ad098c65ca658f3efbbae1efc3a2891ba132041679297402801210274f7bcdf9b6e2d3aa0cded33871f4e4a1956b1c3214b5c65dc7da61306d35d4e00000000
    }

}
