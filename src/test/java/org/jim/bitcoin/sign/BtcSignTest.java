package org.jim.bitcoin.sign;

import org.bitcoinj.core.Transaction;
import org.jim.bitcoin.sign.model.BtcUtxo;
import org.jim.bitcoin.sign.model.BtcVout;
import org.jim.bitcoin.wallet.KeyBag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test BTC tx signature
 *
 * @author JSJ
 */
@RunWith(SpringRunner.class)
public class BtcSignTest {

    /**
     * 单签
     */
    @Test
    public void signTx() {
        // 准备交易
        int network = 1;
        List<BtcUtxo> vinList = new ArrayList<>();
        List<BtcVout> voutList = new ArrayList<>();
        prepareTx(vinList, voutList);

        // 准备私钥
        preparePrivateKey();

        // 签名
        String rawtx = BtcTxSigner.signTx(network, vinList, voutList);
        System.out.println(rawtx);
    }

    private void prepareTx(List<BtcUtxo> vinList, List<BtcVout> voutList) {
        String sender = "moouHqbEsKFo8AVDAEojGVBbjAZXMhr2tx";
        String senderScript = "76a9145af591fba548df828c74970b962162e37a08b80288ac";

        String utxoTxid = "9e16fd673101a942e827c85170edce018549b84dd1d4277122bb8c8018b5949e";
        int utxoVout = 1;

        String receiver = "mnKcaDWTBgeMsWSvStn45Vjv95HPs6fnAg";

        long balance = 5000_0000L;
        long transfer = 1000_0000L;
        long fee = 1000L;
        long change = balance - transfer - fee;

        // 输入，可以扩展到多个
        BtcUtxo utxo1 = BtcUtxo.builder()
                .txid(utxoTxid)
                .n(utxoVout)
                .address(sender)
                .redeemScript(senderScript)
                .build();

        vinList.add(utxo1);

        // 输出，可以扩展到多个
        BtcVout vout1 = BtcVout.builder()
                .address(receiver)
                .amount(transfer)
                .build();
        BtcVout vout2 = BtcVout.builder()
                .address(sender)
                .amount(change)
                .build();

        voutList.add(vout1);
        voutList.add(vout2);
    }

    private void preparePrivateKey() {
        KeyBag keyCache0 = KeyBag.getInstance();

        String sender = "moouHqbEsKFo8AVDAEojGVBbjAZXMhr2tx";
        String prvKey = "f146dbe35a49005800474cd5fa3bd42a8a322ebc11e9cff77f52d9f835224d14";

        keyCache0.addKey(sender, prvKey);
    }


    /**
     * 多签交易签名
     *
     * @throws Exception
     */
    @Test
    public void testSignMultiSigTx() {
        // 构建多签交易
        int network = 1;
        List<BtcUtxo> vinList = new ArrayList<>();
        List<BtcVout> voutList = new ArrayList<>();
        prepareMultiSigTx(vinList, voutList);

        // 准备私钥
        prepareMultiSigPrivateKey();

        // 签名
        Transaction transaction = BtcTxSigner.createRawTx(network, vinList, voutList);
        String sig = BtcMultiSigTxSigner.signTransaction(transaction, vinList);
        System.out.println(sig);
    }

    /**
     * 多签交易拼接
     *
     * @throws Exception
     */
    @Test
    public void testBuildMultiSigTx() throws Exception {
        // 构建多签交易
        int network = 1;
        List<BtcUtxo> vinList = new ArrayList<>();
        List<BtcVout> voutList = new ArrayList<>();
        prepareMultiSigTx(vinList, voutList);

        // 准备签名
        List<String> signatures = prepareMultiSig();

        // 拼接
        String rawTx = BtcMultiSigTxSigner.buildTransaction(network, vinList, voutList, signatures);
        System.out.println(rawTx);
    }

    private void prepareMultiSigTx(List<BtcUtxo> vinList, List<BtcVout> voutList) {
        String sender = "2NGMUmjQX45ZKduVWXGbLXt4FgqNQQoziW2";
        String senderScript = "522102def67575495e212494001307ddd1ded255722713b2fb443cba39a6dde8611aa02102f140886fbd7fb545acc1ad2e2674e0d187daa0e4b5ac255fa8f7195d69f4636652ae";

        String utxoTxid = "0441bdd48897e3cce9dbe17640535e494ee44c1d9ce0cfc508814635d7449412";
        int utxoVout = 0;

        String receiver = "mpYFDz4H2EsDxSpw1r74HMmnnn2BRY6LFC";

        long balance = 5000_0000L;
        long transfer = 1000_0000L;
        long fee = 1000L;
        long change = balance - transfer - fee;

        BtcUtxo utxo1 = BtcUtxo.builder()
                .txid(utxoTxid)
                .n(utxoVout)
                .address(sender)
                .redeemScript(senderScript)
                .build();

        vinList.add(utxo1);

        BtcVout vout1 = BtcVout.builder()
                .address(receiver)
                .amount(transfer)
                .build();
        BtcVout vout2 = BtcVout.builder()
                .address(sender)
                .amount(change)
                .build();

        voutList.add(vout1);
        voutList.add(vout2);
    }

    private void prepareMultiSigPrivateKey() {
        KeyBag keyCache0 = KeyBag.getInstance();

        String sender = "2NGMUmjQX45ZKduVWXGbLXt4FgqNQQoziW2";
        String prvKey = "f146dbe35a49005800474cd5fa3bd42a8a322ebc11e9cff77f52d9f835224d14";

        keyCache0.addKey(sender, prvKey);
    }

    private List<String> prepareMultiSig() {
        return Arrays.asList(
                "3044022046ed295fca0c931f5697246aca6ef06c26a678d0c8331eb50543140ca8f456c702202f4bc0fef68a43e75dce53520eb37488ea00723cc223d156bf082d2f3a85152f",
                "3045022100dab614908090906a07abb27d424827936a08f5c935961dae3215ae20fbf0d7dd022064bc5ead07c6184f7f2a61fa1f67437c00390428e4e42278e00578ae0aabee1d"
        );
    }
}
