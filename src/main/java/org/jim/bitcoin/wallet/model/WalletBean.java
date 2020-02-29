package org.jim.bitcoin.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitcoinj.crypto.DeterministicKey;

import java.util.List;

/**
 * Wallet POJO
 *
 * @author JSJ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBean {
    /**
     * mnemonic
     */
    private List<String> words;
    private String passphrase;

    /**
     * seed
     */
    private byte[] seed;
    private String seedChecksum;

    /**
     * master
     */
    private DeterministicKey m;

    /**
     * encryption
     */
    private byte[] encodedPassword;

}
