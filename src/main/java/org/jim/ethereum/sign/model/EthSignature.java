package org.jim.ethereum.sign.model;

import lombok.Data;

/**
 * ETH Signature
 *
 * @author JSJ
 */
@Data
public class EthSignature {
    private String v;
    private String r;
    private String s;
}
