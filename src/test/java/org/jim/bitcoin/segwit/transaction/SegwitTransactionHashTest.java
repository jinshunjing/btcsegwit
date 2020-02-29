package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SegwitTransactionHashTest {

    @Test
    public void testValue() {
        long v = 3800000L;
        byte[] bytes = new byte[8];
        Utils.uint32ToByteArrayLE(v, bytes, 0);
        System.out.println(Utils.HEX.encode(bytes));
    }

    @Test
    public void testTxid() {
        String txid = "7b2c850111f011349d766c115f9b998ab62f3b74b5bf8c958c762814e8cce8e1";
        txid = Utils.HEX.encode(Utils.reverseBytes(Utils.HEX.decode(txid)));
        System.out.println(txid);
    }

    @Test
    public void testNewTx() {
        String txid = "7b2c850111f011349d766c115f9b998ab62f3b74b5bf8c958c762814e8cce8e1";
        long vout = 0L;

        String addr = "mrwFaerzWhX4W8g5gL2LrjHJzZYdxkw85D";
        long value = 3800000L;

        String rawTx = SegwitTransactionHash.newTx(txid, vout, addr, value);
        System.out.println(rawTx);
        // 0100000001e1e8cce81428768c958cbfb5743b2fb68a999b5f116c769d3411f01101852c7b0000000000ffffffff01c0fb3900000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac00000000
    }

    @Test
    public void testInputSignature() {
        String outpint = "e1e8cce81428768c958cbfb5743b2fb68a999b5f116c769d3411f01101852c7b00000000";
        String amount = "404b4c0000000000";
        String prevSequence = "ffffffff";

        String outputs = "c0fb3900000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac";
        String lockTime = "00000000";

        String redeemScript = "001450507ef7413040b843344d32f74927fcb2fc74a8";
        String prvKey = "L1e26WVtSgYxxXrSBKTZqeJN7d9Tna8r3LUhki6csStu5zCagE1R";

        String txHash = SegwitTransactionHash.hashForP2SHP2WPKH(outpint, amount, prevSequence, redeemScript, outputs, lockTime);

        String signature = SignTransactionService.signInputByWIF(txHash, prvKey);
        System.out.println(signature);
        // 30450221009d0ddf00d710d10f6a4992686ba95e12811f773927a556fad8910cbaa7e08c090220764ae31aac6f94d074f9f758857fbceb70eba4499e5a48684f263065dca59c9a01
    }

    @Test
    public void testSignTx() {
        String rawTx = "0100000001e1e8cce81428768c958cbfb5743b2fb68a999b5f116c769d3411f01101852c7b0000000000ffffffff01c0fb3900000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac00000000";

        String pubKey = "0274f7bcdf9b6e2d3aa0cded33871f4e4a1956b1c3214b5c65dc7da61306d35d4e";
        String redeemScript = "001450507ef7413040b843344d32f74927fcb2fc74a8";

        String signature = "30450221009d0ddf00d710d10f6a4992686ba95e12811f773927a556fad8910cbaa7e08c090220764ae31aac6f94d074f9f758857fbceb70eba4499e5a48684f263065dca59c9a01";

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
        // 01000000000101e1e8cce81428768c958cbfb5743b2fb68a999b5f116c769d3411f01101852c7b000000001716001450507ef7413040b843344d32f74927fcb2fc74a8ffffffff01c0fb3900000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac024830450221009d0ddf00d710d10f6a4992686ba95e12811f773927a556fad8910cbaa7e08c090220764ae31aac6f94d074f9f758857fbceb70eba4499e5a48684f263065dca59c9a01210274f7bcdf9b6e2d3aa0cded33871f4e4a1956b1c3214b5c65dc7da61306d35d4e00000000
    }

}
