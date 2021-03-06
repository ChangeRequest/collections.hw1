package school.lemon.changerequest.java.collections.hw1;

public interface Filter<T> {
    /**
     * Identified if element matches some condition
     *
     * @param element element to match condition
     * @return true, if element matches some condition, otherwise - false
     */
    boolean match(T element);
}
