package com.hpfxd.pandaknockback.profile;

import java.util.Collection;

public interface KnockbackProfileStorage<T extends KnockbackProfile> {
    StorageLoadResult<T, ? extends Throwable> loadProfiles();

    class StorageLoadResult<T extends KnockbackProfile, E extends Throwable> {
        private final Collection<T> successful;
        private final Collection<E> failed;

        public StorageLoadResult(Collection<T> successful, Collection<E> failed) {
            this.successful = successful;
            this.failed = failed;
        }

        public Collection<T> getSuccessful() {
            return this.successful;
        }

        public Collection<E> getFailed() {
            return this.failed;
        }
    }
}
