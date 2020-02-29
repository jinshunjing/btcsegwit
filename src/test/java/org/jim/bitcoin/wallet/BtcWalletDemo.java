package org.jim.bitcoin.wallet;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * Test BTC wallet
 *
 * @author JSJ
 */
@RunWith(SpringRunner.class)
public class BtcWalletDemo {

    private BtcWallet wallet;

    @Before
    public void setUp() {
        wallet = new BtcWallet();
    }

    @Test
    public void testCreateWallet() {
        wallet.createWallet();
    }

    @Test
    public void testImportWallet() {
        String words = "";
        wallet.importWallet(words);
    }

    @Test
    public void testMultiSigAddress() {
        int network = 0;
        int m = 2;
        List<String> pubKeys = Arrays.asList(
                "",
                ""
        );
        Pair<String, String> pair = BtcAddress.toMultiSigAddress(network, m, pubKeys);
        System.out.println(pair.getLeft());
        System.out.println(pair.getRight());
    }

}
