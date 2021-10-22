import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.util.stream.Collectors;

public class Grid {

    public static /*Integer[][]*/Mat getGrid(Mat src, Map<Integer, List<Rect>> boundingRectByArea){

        Mat result = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));

        boundingRectByArea = boundingRectByArea.entrySet().stream()
                .filter(i -> i.getValue().size() >= 4)
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

        Map<Integer, List<Rect>> boundingRectByLines = new HashMap<>();

        int tolleranceInPercents = 15;

        for (Integer key : boundingRectByArea.keySet()) {

            Map<Integer, List<Rect>> mapByX = GroupingBy.approximate(
                    boundingRectByArea.get(key),
                    p->p.x,
                    15
                    );

            for (Rect rect : boundingRectByArea.get(key)) {

                Integer rectX = rect.x;

                int closestX = boundingRectByLines.keySet().stream()
                        .min(Comparator.comparingInt(p -> Math.abs(p - rectX)))
                        .orElse(-1);

                if (closestX > 0 && closestX / 100 * tolleranceInPercents >= Math.abs(closestX - rectX)) {
                    boundingRectByLines.get(closestX).add(rect);
                } else {
                    List<Rect> list = new ArrayList<>();
                    list.add(rect);
                    boundingRectByLines.put(rectX, list);
                    closestX = rectX;
                }

                List<Rect> list = boundingRectByLines.get(closestX);
                for (int i = 0; i < list.size(); i++) {
                    //Imgproc.drawContours(result, list, i, new Scalar(0, 255, 0));
                    Imgproc.rectangle(result, list.get(i), new Scalar(0, 255, 0));
                }
            }
        }
        return result;
    }
}
