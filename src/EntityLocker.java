import java.util.function.Consumer;

public interface EntityLocker {

    void lock(Object key);

    void unlock(Object key);

    void unlockAll();

    default void lockEntity(Entity entity) {
        lock(entity.getId());
    }

    default void unlockEntity(Entity entity) {
        unlock(entity.getId());
    }

    default void executeOnEntity(Entity entity, Consumer<Entity> consumer) {
        lockEntity(entity);
        try {
            consumer.accept(entity);
        } finally {
            unlockEntity(entity);
        }
    }

}
