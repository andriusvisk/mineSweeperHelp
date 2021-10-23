import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.util.stream.Collectors;

public class GridUtils {

    public static Mat getGrid(Mat src, List<MatOfPoint> contours) {

        Mat result = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));

        List<Rect> rectList = contours.stream().map(p -> Imgproc.boundingRect(p)).collect(Collectors.toList());

        rectList = rectList.stream().filter(p -> p.area() > 20).collect(Collectors.toList());

        Map<Integer, List<Rect>> mapByAreaSize = GroupingBy.approximate(rectList, p -> (int) p.area(), 15);

        int minGridCellsCount = 9;
        //min size 3x3
        mapByAreaSize = mapByAreaSize.entrySet().stream().filter(i -> i.getValue().size() >= minGridCellsCount).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

        rectList = mapByAreaSize.entrySet().stream().flatMap(i -> i.getValue().stream()).collect(Collectors.toList());

        Map<Integer, List<Rect>> mapByWidth = GroupingBy.approximate(rectList, p -> p.width, 15);

        Map<Integer, Map<Integer, List<Rect>>> mapByWidthAndHeight = new HashMap<>();

        for (Integer key : mapByWidth.keySet()) {
            Map<Integer, List<Rect>> mapByHeight = GroupingBy.approximate(mapByWidth.get(key), p -> p.height, 15);
            mapByHeight = mapByHeight.entrySet().stream().filter(i -> i.getValue().size() >= minGridCellsCount).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
            mapByWidthAndHeight.put(key, mapByHeight);
        }

        mapByWidthAndHeight.entrySet().stream().flatMap(i -> i.getValue().entrySet().stream()).flatMap(i -> i.getValue().stream())
                .forEach(i -> Imgproc.rectangle(result, i, new Scalar(0, 255, 0)));

        for (Integer widthKey : mapByWidthAndHeight.keySet()) {
            for (Integer heightKey : mapByWidthAndHeight.get(widthKey).keySet()) {
                List<Rect> rects = mapByWidthAndHeight.get(widthKey).get(heightKey);
                Rect mostLeft = rects.stream()
                        .min(Comparator.comparingInt(p->p.x))
                        .orElse(null);


            }
        }

        return result;

    }

    private List<Rect> collectByX(){

    }
}
