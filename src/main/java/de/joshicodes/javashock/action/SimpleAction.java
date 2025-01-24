package de.joshicodes.javashock.action;

import de.joshicodes.javashock.JavaShock;

import java.util.function.Supplier;

/**
 * Just returns the object that was passed to the constructor.
 * @param <T>
 */
public class SimpleAction<T> extends RestAction<T> {

    final Supplier<T> supplier;

    public SimpleAction(final JavaShock instance, final Supplier<T> supplier) {
        super(null, null, null, null);
        this.supplier = supplier;
    }

    @Override
    public T execute() {
        return supplier.get();
    }

}
