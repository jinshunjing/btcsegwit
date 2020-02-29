package org.jim.bitcoin.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;

import java.util.List;

/**
 * Path POJO
 *
 * @author JSJ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathBean {
    /**
     * network
     */
    private NetworkParameters network;

    /**
     * path
     */
    private List<ChildNumber> path;

    /**
     * Key
     */
    private DeterministicKey key;

}
