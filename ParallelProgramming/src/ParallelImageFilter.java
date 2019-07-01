import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelImageFilter extends ImageFilter {

    public ParallelImageFilter(String image) {
        super(image);
    }

    @Override
    public void applyMedianFilter() {
        long start = System.nanoTime();
        //////////////////////Fork join pool 선언 및 execute / join 활용	///////////////////////////////////////////////////////
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        ForkJoinAction forkJoinAction = new ForkJoinAction(this.image, filter_image, 0, width, 0, height);
        pool.execute(forkJoinAction);
        forkJoinAction.join();
        ////////////////////////////////////////////////////////////////////////////////////
        long end = System.nanoTime();
        System.out.println("Parallel Image Filter Median Filter Time : " + (end - start) / 1000000 + " ms");
    }

    private static class ForkJoinAction extends RecursiveAction {
        int[][] image;
        int[][] filter_image;
        private int x1, x2, y1, y2;

        ForkJoinAction(int[][] image, int[][] filter_image, int x1, int x2, int y1, int y2) {
            this.image = image;
            this.filter_image = filter_image;
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
        }

        /////// 위의 applyMedianFilter에서 execute 함수 사용 시 작동하는 함수
        /////// 최소 단위 (가로 * 세로의 값이 32)보다 작으면 중간 값 필터링 이용(ImageFilter.java의 applyMedianFilter 코드 이용)
        /////// 최소 단위보다 클 시 divideTask() 호출
        @Override
        protected void compute() {
            List<ForkJoinAction> subtasks = this.divideTask();
            if (((x2 - x1) * (y2 - y1)) >= 32) {
                subtasks.get(0).fork();//fork로 하나의 process 형성
                subtasks.get(1).compute();//재귀 호출
                subtasks.get(0).join();
            } else {
                for (int i = x1; i < x2; ++i) {
                    for (int j = y1; j < y2; ++j) {
                        int rgb = getMedianValue(image, i, j);//중간 값을 호출
                        filter_image[i][j] = rgb;
                    }
                }
            }
        }

        ////// subtask 생성에 대한 부분 정의하는 곳 (subtaks.add 활용)
        ////// 세로가 가로보다 큰 경우, 세로를 반 나누어 subtask 내의 작업 생성
        ////// 가로가 세로보다 큰 경우, 가로를 반 나누어 subtask 내의 작업 생성
        private List<ForkJoinAction> divideTask() {
            List<ForkJoinAction> subTasks = new ArrayList<>();//2개로나눈 arrayList를 저장 하기 위한 부분
            int widht = this.x2 - this.x1;//width 구함
            int height = this.y2 - this.y1;//heigth 구함
            if (widht > height) {//길이 측정 결과
                subTasks.add(new ForkJoinAction(image, filter_image, this.x1, this.x1 + widht / 2, y1, y2));
                subTasks.add(new ForkJoinAction(image, filter_image, this.x1 + widht / 2, this.x2, y1, y2));
            } else {
                subTasks.add(new ForkJoinAction(image, filter_image, this.x1, this.x2, this.y1, this.y1 + height / 2));
                subTasks.add(new ForkJoinAction(image, filter_image, this.x1, this.x2, this.y1 + height / 2, this.y2));
            }//ArrayList에 추가

            return subTasks;
        }
    }
}
