package school.lemon.changerequest.java.collections.hw1;

public final class ExtendedListFactory {
    private ExtendedListFactory() {

    }

    /**
     * Creates ExtendedList instance
     *
     * @param <E> the type of elements in list
     * @return instance of ExtendedList
     */
    public static <E> ExtendedList<E> create() {
        return new ExtendedListImpl<E>();
    }
}
