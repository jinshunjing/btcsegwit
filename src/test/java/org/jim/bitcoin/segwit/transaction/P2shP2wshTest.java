package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
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
        String txid = "6880f9f2fdb0d4678afc051623b4e0b89a6ba383bbbf179125192e7701af9030";
        long vout = 1L;

        String addr = "mrwFaerzWhX4W8g5gL2LrjHJzZYdxkw85D";
        long value = 2000000L;

        String rawTx = SegwitTransactionHash.newTx(txid, vout, addr, value);
        System.out.println(rawTx);
        // 01000000013090af01772e19259117bfbb83a36b9ab8e0b4231605fc8a67d4b0fdf2f980680100000000ffffffff0180841e00000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac00000000
    }

    /**
     * Get the input signature
     */
    @Test
    public void testInputSignature() {
        String outpint = "3090af01772e19259117bfbb83a36b9ab8e0b4231605fc8a67d4b0fdf2f9806801000000";
        String amount = "c0c62d0000000000";
        String prevSequence = "ffffffff";

        String outputs = "80841e00000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac";
        String lockTime = "00000000";

        String witnessScript = "522102dd0917925edbf521e355b7a1756c4d660be4ed770fb2379d54c400d3ecc7fc6d210397c7ccf98a8b6b7a1f18110124c027664e42f36cfa7779ed233dd13b2f61aa2e2103e7f319a1f02d913f545cd6ac2f938765acea70078a5c80fb120fc286e452e93e53ae";

        //String prvKey = "L3PrLDgmNGinx6fS6FUJooQZu8GKME5bNnMJjRzA7MKt9D4hZqyH";
        String prvKey = "L5SoyU66w6fnFsDDm5LTAb8eNijma2Dn6jYGFsBwfy4F9qqbudQK";
        //String prvKey = "L5aRKaMFWKjcG8AX9mdtcFFYSahVpagQzhnvK8qk2g85YQTVMzE8";

        String txHash = SegwitTransactionHash.hashForP2SHP2WSH(outpint, amount, prevSequence, witnessScript, outputs, lockTime);
        System.out.println(txHash);

        String signature = SignTransactionService.signInputByWIF(txHash, prvKey);
        System.out.println(signature);
        // 304402206e778da7e02fedf211fd07032d7bf0e6e256e61d7ecca582f933c0f1752dbe88022069d99e83471814540d8a3751d2c3d5d2cc55b0dc9b77dc5ef765b18fa48c98ae01
        // 3045022100d399934e3051da1476857b2cd3da718adfd087d49b279a385867485beefdeb9b022042f8558c68a4c5650a1d1a2268173c1ccf4ac79604d526c661bdd48448e2dc7401
    }

    /**
     * Get the signed transaction
     */
    @Test
    public void testSignTx() {
        String rawTx = "01000000013090af01772e19259117bfbb83a36b9ab8e0b4231605fc8a67d4b0fdf2f980680100000000ffffffff0180841e00000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac00000000";

        String redeemScript = "002026038ba9c29e2794b7f33c0d99273593ff57127cbdb84178aec5aceb784602bf";
        String witnessScript = "522102dd0917925edbf521e355b7a1756c4d660be4ed770fb2379d54c400d3ecc7fc6d210397c7ccf98a8b6b7a1f18110124c027664e42f36cfa7779ed233dd13b2f61aa2e2103e7f319a1f02d913f545cd6ac2f938765acea70078a5c80fb120fc286e452e93e53ae";

        String[] signatureScripts = new String[] {
                "304402206e778da7e02fedf211fd07032d7bf0e6e256e61d7ecca582f933c0f1752dbe88022069d99e83471814540d8a3751d2c3d5d2cc55b0dc9b77dc5ef765b18fa48c98ae01",
                "3045022100d399934e3051da1476857b2cd3da718adfd087d49b279a385867485beefdeb9b022042f8558c68a4c5650a1d1a2268173c1ccf4ac79604d526c661bdd48448e2dc7401"
        };

        // parse unsigned tx
        SegwitTransaction transaction = new SegwitTransaction(TestNet3Params.get(), Utils.HEX.decode(rawTx));

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
        // 010000000001013090af01772e19259117bfbb83a36b9ab8e0b4231605fc8a67d4b0fdf2f98068010000002322002026038ba9c29e2794b7f33c0d99273593ff57127cbdb84178aec5aceb784602bfffffffff0180841e00000000001976a9147d41cbe740db118228932b9d6f4799b4803a4f5a88ac040047304402206e778da7e02fedf211fd07032d7bf0e6e256e61d7ecca582f933c0f1752dbe88022069d99e83471814540d8a3751d2c3d5d2cc55b0dc9b77dc5ef765b18fa48c98ae01483045022100d399934e3051da1476857b2cd3da718adfd087d49b279a385867485beefdeb9b022042f8558c68a4c5650a1d1a2268173c1ccf4ac79604d526c661bdd48448e2dc740169522102dd0917925edbf521e355b7a1756c4d660be4ed770fb2379d54c400d3ecc7fc6d210397c7ccf98a8b6b7a1f18110124c027664e42f36cfa7779ed233dd13b2f61aa2e2103e7f319a1f02d913f545cd6ac2f938765acea70078a5c80fb120fc286e452e93e53ae00000000
    }

}
