/* 
 * Copyright (C) 2017 Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
     * Braess Paradox created by Ana L. C. Bazzan in 04/01/201
     */
    BRAESSBAZZAN("BRAESS6"),
    /**
     * EMME TAP.
     */
    EMME("EMME"),
    /**
     * Nguyen and Dupuis (ND) TAP.
     */
    ND("ND"),
    /**
     * Multiobjective Nguyen and Dupuis (ND) TAP.
     */
    OW_MULTIOBJECTIVE("ND_MULTIOBJECTIVE"),
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
    TWO_NEIGHBORHOOD_MIRRORED("TWO_NEIGHBORHOOD_MIRRORED"),
    
    /**
     * Scenario defined by Ana L.C. Bazzan.
     */
    TWO_NEIGHBORHOOD_REPLICATED("TWO_NEIGHBORHOOD_REPLICATED");

    private final String value;

    private ImplementedTAP(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
