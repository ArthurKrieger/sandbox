package dev.arthur.sandbox.counter.domain;

import dev.arthur.sandbox.shared.domain.NotFoundException;
import org.jspecify.annotations.NonNull;

public class CounterNotFoundException extends NotFoundException {

    public CounterNotFoundException(@NonNull CounterId id) {
        super("counter not found: " + id);
    }
}
