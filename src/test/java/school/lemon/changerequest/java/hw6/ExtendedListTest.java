package school.lemon.changerequest.java.hw6;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@RunWith(Suite.class)
@Suite.SuiteClasses({ExtendedListTest.GuavaTests.class, ExtendedListTest.ConditionalIteratorTests.class})
public class ExtendedListTest {

    public static <T> Filter<T> nonNull() {
        return element -> element != null;
    }

    public static class ConditionalIteratorTests {
        @Test(expected = NoSuchElementException.class)
        public void testConditionalIteratorOfEmptyCollection() {
            ExtendedList<Object> extendedList = ExtendedListFactory.create();
            Filter<Object> nonNullFilter = nonNull();
            ConditionalIterator<Object> conditionalIterator = extendedList.conditionalIterator(nonNullFilter);
            Assert.assertEquals(nonNullFilter, conditionalIterator.filter());
            Assert.assertFalse(conditionalIterator.hasNext());
            conditionalIterator.next();
        }

        @Test
        public void testConditionalIteratorWithNoValidElementsInCollection() {
            ExtendedList<Integer> integers = ExtendedListFactory.create();
            integers.add(1);
            integers.add(2);
            integers.add(3);
            ConditionalIterator<Integer> conditionalIterator = integers.conditionalIterator(new Filter<Integer>() {
                @Override
                public boolean match(Integer element) {
                    return element < 0;
                }
            });
            Assert.assertFalse(conditionalIterator.hasNext());
        }

        @Test
        public void testConditionalIteratorWithValidElementsInCollection() {
            ExtendedList<String> strings = ExtendedListFactory.create();
            strings.add("1234567890");
            strings.add("123456789");
            strings.add("12345678");
            strings.add("1234567");
            strings.add("123456");
            strings.add("12345");
            strings.add("1234");
            strings.add("123");
            strings.add("12");
            strings.add("1");
            strings.add("");
            strings.add(null);
            ConditionalIterator<String> conditionalIterator = strings.conditionalIterator(new Filter<String>() {
                @Override
                public boolean match(String element) {
                    return element != null && element.length() > 5;
                }
            });
            int numberOfElements = 0;
            while (conditionalIterator.hasNext()) {
                Assert.assertTrue(conditionalIterator.next().length() > 5);
                numberOfElements++;
            }
            Assert.assertEquals(5, numberOfElements);
        }
    }

    public static class GuavaTests {
        public static TestSuite suite() {
            return ListTestSuiteBuilder.using(new TestStringListGenerator() {
                @Override
                protected List<String> create(String[] elements) {
                    ExtendedList<String> extendedList = ExtendedListFactory.<String>create();
                    assert extendedList != null;
                    extendedList.addAll(Arrays.asList(elements));
                    return extendedList;
                    // uncomment to check that ArrayList implementation do pass all tests
                    // return new ArrayList<String>(Arrays.asList(elements));

                }
            }).named("ExtendedList tests").withFeatures(
                    ListFeature.GENERAL_PURPOSE,
                    CollectionFeature.ALLOWS_NULL_VALUES,
                    CollectionFeature.GENERAL_PURPOSE,
                    CollectionSize.ANY
            ).createTestSuite();
        }
    }
}
