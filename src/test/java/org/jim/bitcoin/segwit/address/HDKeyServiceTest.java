package org.jim.bitcoin.segwit.address;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
public class HDKeyServiceTest {

    @Test
    public void testDeriveAddress() {
        int network = 0;

        // beauty blast arctic west there disease employ nose clinic silly enforce two
        String xPubKey = "xpub6DUFzWDVyVF1yKUhq1QnRrxy8cko4b3Wa3irzUhYuXNcJn4U8Yfropodio8EXKbyDiPsVEWcgvULfvNUnyQVPZLe46uHwMCwMpUz3sdPYN5";

        int changeType = 0;
        int index = 0;

        String address = HDKeyService.deriveWitnessAddress(0, xPubKey, changeType, index);
        System.out.println(address);
    }

    @Test
    public void testDeriveMultiSigAddress() {
        int network = 0;

        int m = 2;
        String[] xPubKeys = new String[] {
                // beauty blast arctic west there disease employ nose clinic silly enforce two
                "xpub6DUFzWDVyVF1yKUhq1QnRrxy8cko4b3Wa3irzUhYuXNcJn4U8Yfropodio8EXKbyDiPsVEWcgvULfvNUnyQVPZLe46uHwMCwMpUz3sdPYN5",
                //
                "xpub6DUFzWDVyVF1yKUhq1QnRrxy8cko4b3Wa3irzUhYuXNcJn4U8Yfropodio8EXKbyDiPsVEWcgvULfvNUnyQVPZLe46uHwMCwMpUz3sdPYN5"
        };

        int changeType = 0;
        int index = 0;

        String address = HDKeyService.deriveWitnessAddress(0, m, Arrays.asList(xPubKeys), changeType, index);
        System.out.println(address);
    }

    @Test
    public void testDeriveScript() {
        // beauty blast arctic west there disease employ nose clinic silly enforce two
        String xPubKey = "xpub6DUFzWDVyVF1yKUhq1QnRrxy8cko4b3Wa3irzUhYuXNcJn4U8Yfropodio8EXKbyDiPsVEWcgvULfvNUnyQVPZLe46uHwMCwMpUz3sdPYN5";

        int changeType = 0;
        int index = 0;

        String[] scripts = HDKeyService.deriveWitnessScript(xPubKey, changeType, index);
        for (String script : scripts) {
            System.out.println(script);
        }
    }

    @Test
    public void testDeriveMultiSigScript() {
        /*
        keyList.clear();
        keyList.add(ECKey.fromPublicOnly(Utils.HEX.decode("0307b8ae49ac90a048e9b53357a2354b3334e9c8bee813ecb98e99a7e07e8c3ba3")));
        keyList.add(ECKey.fromPublicOnly(Utils.HEX.decode("03b28f0c28bfab54554ae8c658ac5c3e0ce6e79ad336331f78c428dd43eea8449b")));
        keyList.add(ECKey.fromPublicOnly(Utils.HEX.decode("034b8113d703413d57761b8b9781957b8c0ac1dfe69f492580ca4195f50376ba4a")));
        keyList.add(ECKey.fromPublicOnly(Utils.HEX.decode("033400f6afecb833092a9a21cfdf1ed1376e58c5d1f47de74683123987e967a8f4")));
        keyList.add(ECKey.fromPublicOnly(Utils.HEX.decode("03a6d48b1131e94ba04d9737d61acdaa1322008af9602b3b14862c07a1789aac16")));
        keyList.add(ECKey.fromPublicOnly(Utils.HEX.decode("02d8b661b0b3302ee2f162b09e07a55ad5dfbe673a9f01d9f0c19617681024306b")));
        */

        int m = 6;
        String[] xPubKeys = new String[] {
                // beauty blast arctic west there disease employ nose clinic silly enforce two
                "xpub6DUFzWDVyVF1yKUhq1QnRrxy8cko4b3Wa3irzUhYuXNcJn4U8Yfropodio8EXKbyDiPsVEWcgvULfvNUnyQVPZLe46uHwMCwMpUz3sdPYN5",
                //
                "xpub6DUFzWDVyVF1yKUhq1QnRrxy8cko4b3Wa3irzUhYuXNcJn4U8Yfropodio8EXKbyDiPsVEWcgvULfvNUnyQVPZLe46uHwMCwMpUz3sdPYN5"
        };

        int changeType = 0;
        int index = 0;

        String[] scripts = HDKeyService.deriveWitnessScript( m, Arrays.asList(xPubKeys), changeType, index);
        for (String script : scripts) {
            System.out.println(script);
        }
    }

}
