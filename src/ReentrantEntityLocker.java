import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ReentrantEntityLocker implements EntityLocker {

    private final Map<Object, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void lock(Object key) {
        ReentrantLock lock = locks.compute(key, (k, oldLock) -> {
            if (oldLock == null) {
                return new ReentrantLock();
            }
            return oldLock;
        });
        lock.lock();
    }

    @Override
    public void unlock(Object key) {
        ReentrantLock lock = locks.computeIfPresent(key, (k, l) -> {
            boolean hasQueuedThreads = l.hasQueuedThreads();
            l.unlock();
            return hasQueuedThreads ? l : null;
        });
        if (lock == null) {
            locks.remove(key, null);
        }
    }

    @Override
    public void unlockAll() {
        List<Object> currentLockKeys = locks.entrySet().stream()
                .filter(entry -> entry.getValue().isHeldByCurrentThread())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        currentLockKeys.forEach(this::unlock);
    }
}
