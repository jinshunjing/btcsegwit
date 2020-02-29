package org.jim.bitcoin.sign.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitcoinj.script.Script;

import java.util.List;

/**
 * BTC vin signature
 *
 * @author JSJ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BtcVinSig {

    private Script redeemScript;
    private Script witnessScript;
    private List<BtcSig> signatures;

}
