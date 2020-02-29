package org.jim.bitcoin.sign.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitcoinj.crypto.TransactionSignature;

/**
 * BTC signature
 *
 * @author JSJ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BtcSig {

    private String pubKey;
    private TransactionSignature signature;

}
