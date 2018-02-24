package school.lemon.changerequest.java.collections.hw1;

import java.util.NoSuchElementException;

public class ConditionalIteratorImpl<E> implements ConditionalIterator<E> {

    Filter<E> filter;
    ExtendedList<E> iteratedList;
    protected int cursor = -1;
    protected int lastPosition = -1;

    ConditionalIteratorImpl(ExtendedList<E> iteratedList, Filter<E> filter) {
        this.filter = filter;
        this.iteratedList = iteratedList;
        cursor = findNext();
    }

    @Override
    public Filter<E> filter() {
        return filter;
    }

    @Override
    public boolean hasNext() {
        return cursor >= 0;
    }

    @Override
    public E next() {
        if (!hasNext())
            throw new NoSuchElementException("List doesn`t contain more elements");
        lastPosition = cursor;
        cursor = findNext();
        return iteratedList.get(lastPosition);
    }

    private int findNext() {
        for (int i = cursor + 1; i < iteratedList.size(); i++)
            if (filter.match(iteratedList.get(i)))
                return i;
        return -1;
    }
}
