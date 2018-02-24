package school.lemon.changerequest.java.collections.hw1;

import java.util.*;

public class ExtendedListImpl<E> implements ExtendedList<E> {

    public static final int INITIAL_ARRAY_SIZE = 10;
    private Object[] container;
    private int size;

    private int modificationCoef = 0;

    ExtendedListImpl() {
        this(INITIAL_ARRAY_SIZE);
    }

    ExtendedListImpl(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Incorrect capacity: " + capacity);
        size = 0;
        container = new Object[capacity];
    }

    ExtendedListImpl(Collection<? extends E> c) {
        notNull(c);
        container = new Object[c.size()];
        addAll(c);
    }

    @Override
    public ConditionalIterator<E> conditionalIterator(Filter<E> filter) throws IllegalArgumentException {
        if (filter == null)
            throw new IllegalArgumentException("Filter can`t be null value");
        return new ConditionalIteratorImpl<>(this, filter);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr(this);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(container, size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        notNull(a);
        if (a.length < size)
            return (T[]) Arrays.copyOf(container, size, a.getClass());
        System.arraycopy((T[]) container, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public boolean add(E e) {
        add(size, e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        notNull(c);
        for (Object o : c)
            if (!contains(o))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        notNull(c);
        return addAll(size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        notNull(c);
        isCorrectIndexForAdd(index);
        int cSize = c.size();
        if (cSize == 0)
            return false;
        if (size + cSize > container.length) {
            int newSize = container.length;
            do {
                newSize *= 2;
            }
            while (newSize < size + cSize);
            Object[] tmp = new Object[newSize];
            System.arraycopy(container, 0, tmp, 0, index);
            System.arraycopy(c.toArray(), 0, tmp, index, cSize);
            System.arraycopy(container, index, tmp, index + cSize, size - index);
            container = tmp;
        } else {
            System.arraycopy(container, index, container, index + cSize, size - index);
            System.arraycopy(c.toArray(), 0, container, index, cSize);
        }
        size += cSize;
        modificationCoef++;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        notNull(c);
        boolean result = false;
        for (int i = 0; i < size; i++)
            if (c.contains(container[i])) {
                remove(i);
                i--;
                result = true;
            }
        if (result)
            modificationCoef++;
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        notNull(c);
        boolean result = false;
        for (int i = 0; i < size; i++) {
            if (!c.contains(container[i])) {
                remove(i);
                i--;
                result = true;
            }
        }
        if (result)
            modificationCoef++;
        return result;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++)
            container[i] = null;
        size = 0;
        modificationCoef++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        isCorrectIndex(index);
        return (E) container[index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        isCorrectIndex(index);
        E result = (E) container[index];
        container[index] = element;
        return result;
    }

    @Override
    public void add(int index, E element) {
        isCorrectIndexForAdd(index);
        if (size == container.length) {
            Object[] tmp = new Object[size * 2];
            System.arraycopy(container, 0, tmp, 0, index);
            tmp[index] = element;
            System.arraycopy(container, index, tmp, index + 1, size - index);
            container = tmp;
        } else {
            System.arraycopy(container, index, container, index + 1, size - index);
            container[index] = element;
        }
        modificationCoef++;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        isCorrectIndex(index);
        E result = (E) container[index];
        System.arraycopy(container, index + 1, container, index, size - index - 1);
        size--;
        return result;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (container[i] == null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(container[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; i--)
                if (container[i] == null)
                    return i;
        } else {
            for (int i = size - 1; i >= 0; i--)
                if (o.equals(container[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListItr(this);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        isCorrectIndexForAdd(index);
        return new ListItr(this, index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        checkSubListIndexes(fromIndex, toIndex);
        return new SubList(this, 0, fromIndex, toIndex);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        if (size > 0)
            result.append(container[0]);
        for (int i = 1; i < size; i++)
            result.append(", ").append(container[i]);
        result.append("]");
        return result.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++)
            result = 31 * result + ((container[i] == null) ? 0 : container[i].hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (o == null)
            return false;

        if (!(o instanceof List))
            return false;

        List tmp = (List) o;
        if (size != tmp.size())
            return false;
        for (int i = 0; i < size; i++)
            if (!areEqual(container[i], tmp.get(i)))
                return false;
        return true;

    }

    private void notNull(Object o) {
        if (o == null)
            throw new NullPointerException();
    }

    private void isCorrectIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Incorrect index:" + index);
    }

    private void isCorrectIndexForAdd(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Incorrect index:" + index);
    }

    private void checkSubListIndexes(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
            throw new IndexOutOfBoundsException("Incorrect index:");
    }

    private boolean areEqual(Object e, Object o) {
        return e == null ? o == null : e.equals(o);
    }


    /**********************************************************************************/
    private class SubList implements List<E> {

        private List<E> parent;
        private int size;
        private int parentBegin;
        private int begin;

        private int modificationCoef;

        SubList(List<E> parent, int begin, int fromIndex, int toIndex) {
            this.parent = parent;
            size = toIndex - fromIndex;
            parentBegin = fromIndex;
            this.begin = begin + fromIndex;
            this.modificationCoef = ExtendedListImpl.this.modificationCoef;
        }

        @Override
        public int size() {
            checkConcurrentModification();
            return size;
        }

        @Override
        public boolean isEmpty() {
            checkConcurrentModification();
            return size == 0;
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        @Override
        public Iterator<E> iterator() {
            checkConcurrentModification();
            return new Itr(this);
        }

        @Override
        public Object[] toArray() {
            checkConcurrentModification();
            Object[] result = new Object[size];
            System.arraycopy(parent.toArray(), parentBegin, result, 0, size);
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            notNull(a);
            checkConcurrentModification();
            T[] buf = parent.toArray(a);
            if (a.length < size) {
                Object[] result = new Object[size];
                System.arraycopy((T[]) buf, parentBegin, result, 0, size);
                return (T[]) result;
            }
            System.arraycopy((T[]) buf, parentBegin, a, 0, size);
            if (a.length > size)
                a[size] = null;
            return a;
        }

        @Override
        public boolean add(E e) {
            checkConcurrentModification();
            parent.add(parentBegin + size, e);
            modificationCoef = ExtendedListImpl.this.modificationCoef;
            size++;
            return true;
        }

        @Override
        public boolean remove(Object o) {
            checkConcurrentModification();
            int index = indexOf(o);
            if (index >= 0) {
                remove(index);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            checkConcurrentModification();
            notNull(c);
            for (Object o : c)
                if (!contains(o))
                    return false;
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return addAll(size, c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            checkConcurrentModification();
            isCorrectIndexForAdd(index);
            parent.addAll(parentBegin + index, c);
            size += c.size();
            modificationCoef = ExtendedListImpl.this.modificationCoef;
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            checkConcurrentModification();
            notNull(c);
            boolean result = false;
            for (int i = 0, j = 0; i < size; i++, j++)
                if (c.contains(container[begin + j])) {
                    remove(j);
                    j--;
                    result = true;
                }
            if (result)
                modificationCoef = ExtendedListImpl.this.modificationCoef;
            return result;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            checkConcurrentModification();
            notNull(c);
            boolean result = false;
            for (int i = 0, j = 0; i < size; i++, j++)
                if (!c.contains(container[begin + j])) {
                    remove(j);
                    j--;
                    result = true;
                }
            if (result)
                modificationCoef = ExtendedListImpl.this.modificationCoef;
            return result;
        }

        @Override
        public void clear() {
            checkConcurrentModification();
            for (int i = 0; i < size; i++)
                parent.remove(parentBegin);
            size = 0;
            modificationCoef = ExtendedListImpl.this.modificationCoef;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;

            if (o == null)
                return false;

            if (!(o instanceof List))
                return false;

            List tmp = (List) o;
            if (size != tmp.size())
                return false;
            for (int i = 0; i < size; i++)
                if (!areEqual(container[begin + i], tmp.get(i)))
                    return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            for (int i = 0; i < size; i++)
                result = 31 * result + ((container[begin + i] == null) ? 0 : container[begin + i].hashCode());
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E get(int index) {
            checkConcurrentModification();
            isCorrectIndex(index);
            return (E) ExtendedListImpl.this.container[begin + index];
        }

        @Override
        public E set(int index, E element) {
            isCorrectIndex(index);
            checkConcurrentModification();
            return parent.set(parentBegin + index, element);
        }

        @Override
        public void add(int index, E element) {
            checkConcurrentModification();
            isCorrectIndexForAdd(index);
            parent.add(parentBegin + index, element);
            modificationCoef = ExtendedListImpl.this.modificationCoef;
        }

        @Override
        public E remove(int index) {
            checkConcurrentModification();
            isCorrectIndex(index);
            E result = parent.get(parentBegin + index);
            parent.remove(parentBegin + index);
            modificationCoef = ExtendedListImpl.this.modificationCoef;
            size--;
            return result;
        }

        @Override
        public int indexOf(Object o) {
            checkConcurrentModification();
            if (o == null) {
                for (int i = 0; i < size; i++)
                    if (ExtendedListImpl.this.container[begin + i] == null)
                        return i;
            } else {
                for (int i = 0; i < size; i++)
                    if (o.equals(ExtendedListImpl.this.container[begin + i]))
                        return i;
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            checkConcurrentModification();
            if (o == null) {
                for (int i = size - 1; i >= 0; i--)
                    if (ExtendedListImpl.this.container[begin + i] == null)
                        return i;
            } else {
                for (int i = size - 1; i >= 0; i--)
                    if (o.equals(ExtendedListImpl.this.container[begin + i]))
                        return i;
            }
            return -1;
        }

        @Override
        public ListIterator<E> listIterator() {
            checkConcurrentModification();
            return new ListItr(this);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            checkConcurrentModification();
            isCorrectIndexForAdd(index);
            return new ListItr(this, index);
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            checkSubListIndexes(fromIndex, toIndex);
            checkConcurrentModification();
            return new SubList(this, begin, fromIndex, toIndex);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder("[");
            if (size > 0)
                result.append(ExtendedListImpl.this.container[begin]);
            for (int i = 1; i < size; i++)
                result.append(", ").append(ExtendedListImpl.this.container[begin + i]);
            result.append("]");
            return result.toString();
        }

        private void isCorrectIndex(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("Incorrect index:" + index);
        }

        private void isCorrectIndexForAdd(int index) {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("Incorrect index:" + index);
        }

        private void checkSubListIndexes(int fromIndex, int toIndex) {
            if (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
                throw new IndexOutOfBoundsException("Incorrect index:");
        }

        private void checkConcurrentModification() {
            if (modificationCoef != ExtendedListImpl.this.modificationCoef)
                throw new ConcurrentModificationException();
        }

    }


    /**********************************************************************************/

    private class ListItr extends Itr implements ListIterator<E> {

        ListItr(List<E> iteratedList) {
            this(iteratedList, 0);
        }

        ListItr(List<E> iteratedList, int index) {
            super(iteratedList);
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious())
                throw new NoSuchElementException("List doesn`t contain more elements");
            lastPosition = --cursor;
            return iteratedList.get(cursor);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(E e) {
            if (lastPosition < 0)
                throw new IllegalStateException("Method next or previous was not called");
            iteratedList.set(lastPosition, e);
        }

        @Override
        public void add(E e) {
            iteratedList.add(cursor, e);
            cursor++;
            lastPosition = -1;
        }
    }

    /**********************************************************************************/

    private class Itr implements Iterator<E> {
        protected int cursor = 0;
        protected int lastPosition = -1;
        protected List<E> iteratedList;


        Itr(List<E> iteratedList) {
            this.iteratedList = iteratedList;
        }

        @Override
        public boolean hasNext() {
            return cursor < iteratedList.size();
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException("List doesn`t contain more elements");
            lastPosition = cursor;
            cursor++;
            return iteratedList.get(lastPosition);
        }

        @Override
        public void remove() {
            if (lastPosition < 0)
                throw new IllegalStateException("Method next was not called");
            iteratedList.remove(lastPosition);
            if (lastPosition < cursor)
                cursor--;
            lastPosition = -1;
        }
    }
}
