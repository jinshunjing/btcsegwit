package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet2Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test P2SH(P2WSH)
 */
@RunWith(SpringRunner.class)
public class P2shP2wshTest {

    /**
     * Create a simple transaction
     */
    @Test
    public void testNewTx() {
        String txid = "0756e7bf524137d43a504a4b50eabacd2b3cafcfb65b5e5a3a922983df8fa95d";
        long vout = 0L;

        String addr = "mrwFaerzWhX4W8g5gL2LrjHJzZYdxkw85D";
        long value = 2800000L;

        String rawTx = SegwitTransactionHash.newTx(txid, vout, addr, value);
        System.out.println(rawTx);
        // 01000000015da98fdf8329923a5a5e5bb6cfaf3c2bcdbaea504b4a503ad4374152bfe756070000000000ffffffff0180b92a00000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac00000000
    }

    /**
     * Get the input signature
     */
    @Test
    public void testInputSignature() {
        String outpint = "5da98fdf8329923a5a5e5bb6cfaf3c2bcdbaea504b4a503ad4374152bfe7560700000000";
        String amount = "c0c62d0000000000";
        String prevSequence = "ffffffff";

        String outputs = "80b92a00000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac";
        String lockTime = "00000000";

        String witnessScript = "522102dd0917925edbf521e355b7a1756c4d660be4ed770fb2379d54c400d3ecc7fc6d210397c7ccf98a8b6b7a1f18110124c027664e42f36cfa7779ed233dd13b2f61aa2e2103e7f319a1f02d913f545cd6ac2f938765acea70078a5c80fb120fc286e452e93e53ae";

        //String prvKey = "L3PrLDgmNGinx6fS6FUJooQZu8GKME5bNnMJjRzA7MKt9D4hZqyH";
        //String prvKey = "L5SoyU66w6fnFsDDm5LTAb8eNijma2Dn6jYGFsBwfy4F9qqbudQK";
        String prvKey = "L5aRKaMFWKjcG8AX9mdtcFFYSahVpagQzhnvK8qk2g85YQTVMzE8";

        String txHash = SegwitTransactionHash.hashForP2SHP2WSH(outpint, amount, prevSequence, witnessScript, outputs, lockTime);
        System.out.println(txHash);

        String signature = SignTransactionService.signInputByWIF(txHash, prvKey);
        System.out.println(signature);
        // 304402203a3fd334928fdb951f6377d1466ef5dd64ecbd0ec1c8ecbc74a0ea8253ac35e002205a300b9cb002468e2ca057a98688a1fa2b754f5d96906bfea099035e209a04d8
        // 3045022100cba69b0bdca4af77c469956151f098c4e32466e83c783c560bfd506f2c747e580220537c77690ed04d355c75de51d2ea22c34e272291c1208c507a0ca1712428ba41
        // 304402202448aa2170d7b4bf6b931fb826fa2bafbc12a5a83d2f16bc699a1d98ceaa8f7c02206c82b27001697042b7b95a12aed5bcb4ec4c33be9cf3c5eddf788a867937c8a1
    }

    /**
     * Get the signed transaction
     */
    @Test
    public void testSignTx() {
        String rawTx = "01000000015da98fdf8329923a5a5e5bb6cfaf3c2bcdbaea504b4a503ad4374152bfe756070000000000ffffffff0180b92a00000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac00000000";

        String redeemScript = "002026038ba9c29e2794b7f33c0d99273593ff57127cbdb84178aec5aceb784602bf";
        String witnessScript = "522102dd0917925edbf521e355b7a1756c4d660be4ed770fb2379d54c400d3ecc7fc6d210397c7ccf98a8b6b7a1f18110124c027664e42f36cfa7779ed233dd13b2f61aa2e2103e7f319a1f02d913f545cd6ac2f938765acea70078a5c80fb120fc286e452e93e53ae";
        String[] signatureScripts = new String[] {
                "304402203a3fd334928fdb951f6377d1466ef5dd64ecbd0ec1c8ecbc74a0ea8253ac35e002205a300b9cb002468e2ca057a98688a1fa2b754f5d96906bfea099035e209a04d801",
                "3045022100cba69b0bdca4af77c469956151f098c4e32466e83c783c560bfd506f2c747e580220537c77690ed04d355c75de51d2ea22c34e272291c1208c507a0ca1712428ba4101"
        };

        // parse unsigned tx
        SegwitTransaction transaction = new SegwitTransaction(TestNet2Params.get(), Utils.HEX.decode(rawTx));

        // add redeem script
        Script inputScript = (new ScriptBuilder()).data(Utils.HEX.decode(redeemScript)).build();
        transaction.getInput(0).setScriptSig(inputScript);

        // add witness script
        TransactionWitness txWitness = new TransactionWitness();
        txWitness.addPush(new byte[0]);
        for (String sigHex : signatureScripts) {
            txWitness.addPush(Utils.HEX.decode(sigHex));
        }
        txWitness.addPush(Utils.HEX.decode(witnessScript));
        transaction.setWitness(0, txWitness);

        // generate the signed tx
        String signedRawTx = Utils.HEX.encode(transaction.bitcoinSerialize());
        System.out.println(signedRawTx);
        // 010000000001015da98fdf8329923a5a5e5bb6cfaf3c2bcdbaea504b4a503ad4374152bfe75607000000002322002026038ba9c29e2794b7f33c0d99273593ff57127cbdb84178aec5aceb784602bfffffffff0180b92a00000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac040047304402203a3fd334928fdb951f6377d1466ef5dd64ecbd0ec1c8ecbc74a0ea8253ac35e002205a300b9cb002468e2ca057a98688a1fa2b754f5d96906bfea099035e209a04d801483045022100cba69b0bdca4af77c469956151f098c4e32466e83c783c560bfd506f2c747e580220537c77690ed04d355c75de51d2ea22c34e272291c1208c507a0ca1712428ba410169522102dd0917925edbf521e355b7a1756c4d660be4ed770fb2379d54c400d3ecc7fc6d210397c7ccf98a8b6b7a1f18110124c027664e42f36cfa7779ed233dd13b2f61aa2e2103e7f319a1f02d913f545cd6ac2f938765acea70078a5c80fb120fc286e452e93e53ae00000000
    }

}
