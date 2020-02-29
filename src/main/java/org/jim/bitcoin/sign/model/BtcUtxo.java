package org.jim.bitcoin.sign.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BTC UTXO
 *
 * @author JSJ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BtcUtxo {

    private String txid;
    private int n;

    private String address;
    private String redeemScript;
    private String pubKey;

    private long amount;

}
