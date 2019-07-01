import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
//        ImageFilter filter = new ImageFilter(Paths.get("gray_earth.png").toString());
//        filter.applyMedianFilter();
//        filter.writeFilterImage();
        ParallelImageFilter parallelImageFilter = new ParallelImageFilter(Paths.get("gray_earth.png").toString());
        parallelImageFilter.applyMedianFilter();
        parallelImageFilter.writeFilterImage();
    }
}
