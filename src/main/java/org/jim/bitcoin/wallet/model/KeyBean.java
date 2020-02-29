package org.jim.bitcoin.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Key JavaBean
 *
 * @author JSJ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyBean {
    /**
     * index
     */
    private String index;

    /**
     * public key
     */
    private String pubkey;

    /**
     * private key
     */
    private String prvkey;
}
