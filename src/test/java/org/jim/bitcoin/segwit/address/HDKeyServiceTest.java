package org.jim.bitcoin.segwit.address;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
public class HDKeyServiceTest {

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
        // 0274f7bcdf9b6e2d3aa0cded33871f4e4a1956b1c3214b5c65dc7da61306d35d4e
        // 001450507ef7413040b843344d32f74927fcb2fc74a8
    }

    @Test
    public void testDeriveAddress() {
        int network = 0;

        // beauty blast arctic west there disease employ nose clinic silly enforce two
        String xPubKey = "xpub6DUFzWDVyVF1yKUhq1QnRrxy8cko4b3Wa3irzUhYuXNcJn4U8Yfropodio8EXKbyDiPsVEWcgvULfvNUnyQVPZLe46uHwMCwMpUz3sdPYN5";

        int changeType = 0;
        int index = 0;

        String address = HDKeyService.deriveWitnessAddress(network, xPubKey, changeType, index);
        System.out.println(address);
        // 2N2WL1m3Gz2kYAPuEhAsLLrDSM2qgqdG65N
    }

    @Test
    public void testDeriveMultiSigScript() {
        int m = 2;

        String[] xPubKeys = new String[] {
                // sense neutral mouse praise pause able have immense domain grass gesture goat
                "xpub6CauQQeMHMPoz83GgntV8RnT8BrErgPcGyHspePmKsQznJGfP1anALMdnQGj9ExmqHxchuPATjGnA5n9Qj5yjbFMur27PzvXBxk6zqc8gKR",
                // submit short jelly wolf tragic congress bench abandon reason frost recipe design
                "xpub6CBdjTfG71uiuyd5VUZZPf6eKK4UswMwsvJv3LnpfmwkeDa2U6XwEwUj5BJm4cFfHZMf5nBJKaJbLZVYfD7AP9UWB4aJ3Mbb9nbAhXWWgAU",
                // carbon task under clip work someone stairs cruise achieve green elbow stove
                "xpub6C688CScVWhjfSuvd8SCRPVL6ovAXCNE7F6C5ZLb7svMXkUMEnYQ6n798GFhYTiEVUFzJEVbFyPSsQQXCgnpaYTPbnhkNngfofCQDrvFD8v"
        };

        int changeType = 0;
        int index = 0;
        // L3PrLDgmNGinx6fS6FUJooQZu8GKME5bNnMJjRzA7MKt9D4hZqyH
        // L5SoyU66w6fnFsDDm5LTAb8eNijma2Dn6jYGFsBwfy4F9qqbudQK
        // L5aRKaMFWKjcG8AX9mdtcFFYSahVpagQzhnvK8qk2g85YQTVMzE8

        String[] scripts = HDKeyService.deriveWitnessScript(m, Arrays.asList(xPubKeys), changeType, index);
        for (String script : scripts) {
            System.out.println(script);
        }
        // a9142e8772750b46d0ac4d74cfedc4f51b77e5a6e09787
        // 002026038ba9c29e2794b7f33c0d99273593ff57127cbdb84178aec5aceb784602bf
        // 522102dd0917925edbf521e355b7a1756c4d660be4ed770fb2379d54c400d3ecc7fc6d210397c7ccf98a8b6b7a1f18110124c027664e42f36cfa7779ed233dd13b2f61aa2e2103e7f319a1f02d913f545cd6ac2f938765acea70078a5c80fb120fc286e452e93e53ae
    }

    @Test
    public void testDeriveMultiSigAddress() {
        int network = 1;

        int m = 2;
        String[] xPubKeys = new String[] {
                // sense neutral mouse praise pause able have immense domain grass gesture goat
                "xpub6CauQQeMHMPoz83GgntV8RnT8BrErgPcGyHspePmKsQznJGfP1anALMdnQGj9ExmqHxchuPATjGnA5n9Qj5yjbFMur27PzvXBxk6zqc8gKR",
                // submit short jelly wolf tragic congress bench abandon reason frost recipe design
                "xpub6CBdjTfG71uiuyd5VUZZPf6eKK4UswMwsvJv3LnpfmwkeDa2U6XwEwUj5BJm4cFfHZMf5nBJKaJbLZVYfD7AP9UWB4aJ3Mbb9nbAhXWWgAU",
                // carbon task under clip work someone stairs cruise achieve green elbow stove
                "xpub6C688CScVWhjfSuvd8SCRPVL6ovAXCNE7F6C5ZLb7svMXkUMEnYQ6n798GFhYTiEVUFzJEVbFyPSsQQXCgnpaYTPbnhkNngfofCQDrvFD8v"
        };

        int changeType = 0;
        int index = 0;

        String address = HDKeyService.deriveWitnessAddress(network, m, Arrays.asList(xPubKeys), changeType, index);
        System.out.println(address);
        // 2MwVFL8vw3hLQTrvTfExeJV6BhERiKfgd3E
    }

}
