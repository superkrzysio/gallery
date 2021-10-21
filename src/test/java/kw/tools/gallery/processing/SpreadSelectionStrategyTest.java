package kw.tools.gallery.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class ThumbnailSelectionFactoryTest
{
    private final static ThumbnailSelectionFactory factory = new ThumbnailSelectionFactory();

    @ParameterizedTest
    @MethodSource("getTestPairs")
    public void testSpreadSelection(int sourceSize, int count)
    {
        List<String> testImages = listOf(sourceSize);
        ThumbnailSelector selector = factory.get(ThumbnailSelector.Strategy.SPREAD, count);
        List<String> result = selector.select(testImages);
        int expectedSize = Math.min(sourceSize, count);
        System.out.println("Elements: " + String.join(", ", result));
        Assertions.assertEquals(expectedSize, result.size());

        if (sourceSize >= 1 && count >= 1)
        {
            // first
            Assertions.assertEquals(testImages.get(0), result.get(0));

            if (count > 1)
                // last
                Assertions.assertEquals(testImages.get(testImages.size()-1), result.get(expectedSize-1));
        }
    }

    @ParameterizedTest
    @MethodSource("getTestPairs")
    public void testFirstSelection(int sourceSize, int count)
    {
        List<String> testImages = listOf(sourceSize);
        ThumbnailSelector selector = factory.get(ThumbnailSelector.Strategy.FIRST, count);
        int expectedSize = Math.min(sourceSize, count);
        List<String> result = selector.select(testImages);
        Assertions.assertEquals(expectedSize, result.size());
        System.out.println("Elements: " + String.join(", ", result));
        if (sourceSize >= 1 && count >= 1)
        {
            // first element
            Assertions.assertEquals(testImages.get(0), result.get(0));

            if (count > 1)
                // last element
                Assertions.assertEquals(testImages.get(expectedSize - 1), result.get(expectedSize-1));
        }
    }

    @ParameterizedTest
    @MethodSource("getTestPairs")
    public void testLastSelection(int sourceSize, int count)
    {
        List<String> testImages = listOf(sourceSize);
        ThumbnailSelector selector = factory.get(ThumbnailSelector.Strategy.LAST, count);
        int expectedSize = Math.min(sourceSize, count);
        List<String> result = selector.select(testImages);
        Assertions.assertEquals(expectedSize, result.size());
        System.out.println("Elements: " + String.join(", ", result));
        if (sourceSize >= 1 && count >= 1)
        {
            // last element
            Assertions.assertEquals(testImages.get(testImages.size()-1), result.get(expectedSize-1));

            if (count > 1)
                // first element
                Assertions.assertEquals(testImages.get(testImages.size() - expectedSize), result.get(0));
        }
    }

    private static Stream<Arguments> getTestPairs()
    {
        return Stream.of(
                Arguments.of(0, 0),

                Arguments.of(1, 0),
                Arguments.of(0, 1),

                Arguments.of(0, 2),
                Arguments.of(1, 2),
                Arguments.of(2, 2),
                Arguments.of(2, 0),
                Arguments.of(2, 1),
                Arguments.of(2, 2),

                Arguments.of(3, 0),
                Arguments.of(3, 1),
                Arguments.of(3, 2),
                Arguments.of(3, 3),
                Arguments.of(0, 3),
                Arguments.of(1, 3),
                Arguments.of(2, 3),
                Arguments.of(3, 3),

                Arguments.of(5, 0),
                Arguments.of(5, 1),
                Arguments.of(5, 2),
                Arguments.of(5, 3),
                Arguments.of(5, 4),
                Arguments.of(5, 5),
                Arguments.of(0, 5),
                Arguments.of(1, 5),
                Arguments.of(2, 5),
                Arguments.of(3, 5),
                Arguments.of(4, 5),
                Arguments.of(5, 5)
        );
    }

    private List<String> listOf(int size)
    {
        return IntStream.range(0, size).mapToObj(Integer::toString).collect(Collectors.toList());
    }
}