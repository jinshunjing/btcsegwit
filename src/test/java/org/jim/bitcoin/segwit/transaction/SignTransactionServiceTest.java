package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SignTransactionServiceTest {

    @Test
    public void testSign() {
        String raw = "01000000b0287b4a252ac05af83d2dcef00ba313af78a3e9c329afa216eb3aa2a7b4613a18606b350cd8bf565266bc352f0caddcf01e8fa789dd8a15386327cf8cabe198db6b1b20aa0fd7b23880be2ecbd4a98130974cf4748fb66092ac4d3ceb1a5477010000001976a91479091972186c449eb1ded22b78e40d009bdf008988ac00ca9a3b00000000feffffffde984f44532e2173ca0d64314fcefe6d30da6f8cf27bafa706da61df8a226c839204000001000000";
        raw = "db6b1b20aa0fd7b23880be2ecbd4a98130974cf4748fb66092ac4d3ceb1a547701000000";

        Sha256Hash hash = Sha256Hash.twiceOf(Utils.HEX.decode(raw));
        System.out.println(hash.toString());
        //64f3b0f4dd2bb3aa1ce8566d220cc74dda9df97d8490cc81d89d735c92e59fb6
        //64f3b0f4dd2bb3aa1ce8566d220cc74dda9df97d8490cc81d89d735c92e59fb6
    }

    @Test
    public void testSingP2WPKHInput() {
        int network = 0;
        String rawTx = "0100000001db6b1b20aa0fd7b23880be2ecbd4a98130974cf4748fb66092ac4d3ceb1a54770100000000feffffff02b8b4eb0b000000001976a914a457b684d7f0d539a46a45bbc043f35b59d0d96388ac0008af2f000000001976a914fd270b1ee6abcaea97fea7ad0402e8bd8ad6d77c88ac92040000";
        int vin = 0;
        String prevKey = "eb696a065ef48a2192da5b28b694f87544b30fae8327c4510137a922f32c6dcf";
        String scriptPubKey = "a9144733f37cf4db86fbc2efed2500b4f4e49f31202387";
        String redeemScript = "001479091972186c449eb1ded22b78e40d009bdf0089";

        String signature = SignTransactionService.signInput(network, rawTx, vin, prevKey, scriptPubKey, redeemScript);
        System.out.println(signature);
    }

    @Test
    public void testSingP2WSHInput() {
        int network = 0;
        String rawTx = "010000000136641869ca081e70f394c6948e8af409e18b619df2ed74aa106c1ca29787b96e0100000000ffffffff0200e9a435000000001976a914389ffce9cd9ae88dcc0631e88a821ffdbe9bfe2688acc0832f05000000001976a9147480a33f950689af511e6e84c138dbbd3c3ee41588ac00000000";
        int vin = 0;
        String prevKey = "730fff80e1413068a05b57d6a58261f07551163369787f349438ea38ca80fac6";
        String scriptPubKey = "a9149993a429037b5d912407a71c252019287b8d27a587";
        String redeemScript = "56210307b8ae49ac90a048e9b53357a2354b3334e9c8bee813ecb98e99a7e07e8c3ba32103b28f0c28bfab54554ae8c658ac5c3e0ce6e79ad336331f78c428dd43eea8449b21034b8113d703413d57761b8b9781957b8c0ac1dfe69f492580ca4195f50376ba4a21033400f6afecb833092a9a21cfdf1ed1376e58c5d1f47de74683123987e967a8f42103a6d48b1131e94ba04d9737d61acdaa1322008af9602b3b14862c07a1789aac162102d8b661b0b3302ee2f162b09e07a55ad5dfbe673a9f01d9f0c19617681024306b56ae";

        String signature = SignTransactionService.signInput(network, rawTx, vin, prevKey, scriptPubKey, redeemScript);
        System.out.println(signature);
    }

}
