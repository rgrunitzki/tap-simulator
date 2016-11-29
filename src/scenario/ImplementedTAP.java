/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario;

/**
 *
 * @author Ricardo Grunitzki
 */
public enum ImplementedTAP {

    /**
     * Bypass TAP.
     */
    BYPASS("BYPASS"),
    /**
     * Braess Paradox TAP.
     */
    BRAESS("BRAESS"),
    /**
     * Braess Paradox with 6 trips TAP.
     */
    BRAESS6("BRAESS6"),
    /**
     * EMME TAP.
     */
    EMME("EMME"),
    /**
     * Nguyen and Dupuis (ND) TAP.
     */
    ND("ND"),
    /**
     * Ortuzar and Willumsen (OW) TAP.
     */
    OW("OW"),
    /**
     * Sioux Falls (SF) TAP.
     */
    SF("SF"),
    /**
     * Scenario defined by Ana L.C. Bazzan.
     */
    TWO_NEIGHBORHOOD("TWO_NEIGHBORHOOD"),
    /**
     * Scenario defined by Ana L.C. Bazzan.
     */
    SYMMETRICAL_2NEIGHBORHOOD("SYMMETRICAL_2NEIGHBORHOOD");

    private final String value;

    private ImplementedTAP(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
