package org.example.stats;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class StatCollector {
    private StatCollector() {}

    @SafeVarargs
    public static Set<Stat> of(Class<? extends Stat>... enums) {
        final var stats = new LinkedHashSet<Stat>();
        Arrays.stream(enums)
                .flatMap(e -> Arrays.stream(e.getEnumConstants()))
                .forEach(stats::add);
        return stats;
    }
}