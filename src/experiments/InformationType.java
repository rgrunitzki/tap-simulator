/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments;

/**
 *
 * @author rgrunitzki
 */
public enum InformationType {

    Average("Average Information"),
    Best("Best Information"),
    Last("Last Information"),
    None("No Information");

    private final String value;

    private InformationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
