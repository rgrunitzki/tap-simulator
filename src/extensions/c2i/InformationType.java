/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.c2i;

/**
 *
 * @author Ricardo Grunitzki
 */
public enum InformationType {

    Average("avg"),
    Best("best"),
    Last("last"),
    None("none");

    private final String value;

    private InformationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}