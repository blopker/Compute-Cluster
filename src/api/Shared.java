/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.io.Serializable;

/**
 *
 * @author ninj0x
 */
public abstract class Shared<T> implements Serializable{

    private T shared;

    public Shared(T shared) {
        this.shared = shared;
    }

    public abstract boolean isBetterThan(Shared<T> other);

    public T getShared() {
        return shared;
    }
}
