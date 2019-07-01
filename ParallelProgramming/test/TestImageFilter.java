import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


public class TestImageFilter {
    @Test
    public void TestFindNth() {
        int[] temp = new int[]{1, 11, 17, 3, 2, 5, 7};
        int mid = ImageFilter.findNth(temp, 0, temp.length - 1, temp.length / 2);
        Assertions.assertEquals(mid, 5);
    }

    @Test
    void TestEqualsOfParallel() {
        String path = Paths.get("test_image.png").toString();
        ImageFilter imageFilter = new ImageFilter(path);
        ParallelImageFilter parallelImageFilter = new ParallelImageFilter(path);

        imageFilter.applyMedianFilter();
        parallelImageFilter.applyMedianFilter();

        assertArrayEquals(imageFilter.filter_image, parallelImageFilter.filter_image);
    }
}
