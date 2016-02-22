/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario;

/**
 *
 * @author rgrunitzki
 */
public enum ImplementedTAP {
    
    ANA("ANA"),
    BYPASS("BYPASS"),
    BRAESS("BRAESS"),
    EMME("EMME"),
    ND("ND"),
    OW("OW"),
    SF("SF");

    private final String value;

    private ImplementedTAP(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}