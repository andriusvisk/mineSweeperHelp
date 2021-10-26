import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.util.stream.Collectors;

public class GridUtils {

    public static Mat getGrid(Mat src, List<MatOfPoint> contours) {

        Mat result = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));

        List<Rect> rectList = contours.stream().map(p -> Imgproc.boundingRect(p)).collect(Collectors.toList());

        rectList = rectList.stream().filter(p -> p.area() > 20).collect(Collectors.toList());

        int tolleranceInPercent = 15;

        Map<Integer, List<Rect>> mapByAreaSize = GroupingBy.approximate(rectList, p -> (int) p.area(), tolleranceInPercent);

        //min size 9x9
        int minGridCellsCount = 81;

        mapByAreaSize = mapByAreaSize.entrySet().stream().filter(i -> i.getValue().size() >= minGridCellsCount).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

        rectList = mapByAreaSize.entrySet().stream().flatMap(i -> i.getValue().stream()).collect(Collectors.toList());

        Map<Integer, List<Rect>> mapByWidth = GroupingBy.approximate(rectList, p -> p.width, tolleranceInPercent);

        Map<Integer, Map<Integer, List<Rect>>> mapByWidthAndHeight = new HashMap<>();

        for (Integer key : mapByWidth.keySet()) {
            Map<Integer, List<Rect>> mapByHeight = GroupingBy.approximate(mapByWidth.get(key), p -> p.height, tolleranceInPercent);
            mapByHeight = mapByHeight.entrySet().stream().filter(i -> i.getValue().size() >= minGridCellsCount).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
            mapByWidthAndHeight.put(key, mapByHeight);
        }

        /*mapByWidthAndHeight.entrySet().stream().flatMap(i -> i.getValue().entrySet().stream()).flatMap(i -> i.getValue().stream())
                .forEach(i -> Imgproc.rectangle(result, i, new Scalar(0, 255, 0)));*/

        for (Integer widthKey : mapByWidthAndHeight.keySet()) {
            for (Integer heightKey : mapByWidthAndHeight.get(widthKey).keySet()) {

                Grid grid = collectGrid(mapByWidthAndHeight.get(widthKey).get(heightKey), tolleranceInPercent, result);

            }
        }

        return result;

    }

    private static Grid collectGrid(List<Rect> rects, int tolleranceInPercent, Mat result) {

        Rect top = rects.stream().min(Comparator.comparingInt(p -> p.y)).orElse(null);

        if (top != null) {
            List<Rect> topLine = new ArrayList();

            while (topLine != null && topLine.size() < 9) {

                final Rect topFinal = top;

                topLine = rects.stream().filter(p -> Math.abs(p.y - topFinal.y) <= (double) topFinal.height / 100 * tolleranceInPercent)
                        .collect(Collectors.toList());

                if (topLine.size() == 0)
                    topLine = null;
                else if (topLine.size() < 9) {
                    rects.remove(top);
                    top = rects.stream().min(Comparator.comparingInt(p -> p.y)).orElse(null);
                }

            }

            if (topLine != null) {

                topLine.sort(Comparator.comparingInt(p -> p.x));

                if (checkLineIntegrity(topLine)) {
                    //topLine.stream().forEach(i -> Imgproc.rectangle(result, i, new Scalar(0, 255, 0)));
                    rects.removeAll(topLine);
                    List<List<Rect>> allLines = getAllLines(rects, topLine, tolleranceInPercent);

                    Grid grid = new Grid(allLines);

                    allLines.stream().flatMap(p -> p.stream()).forEach(i -> Imgproc.rectangle(result, i, new Scalar(0, 255, 0)));
                }

            }
        }

        return null;
    }

    private static List<List<Rect>> getAllLines(List<Rect> rects, List<Rect> topLine, int tolleranceInPercent) {

        List<List<Rect>> restLines = new ArrayList<>();
        restLines.add(topLine);

        while (true) {
            List<Rect> topLineFinal = topLine;
            List<Rect> newLine = rects.stream()
                    .filter(
                            p -> p.y - (topLineFinal.get(0).y + topLineFinal.get(0).height) >= 0
                                    && p.y - (topLineFinal.get(0).y + topLineFinal.get(0).height) <= (double) topLineFinal.get(0).height / 100 * tolleranceInPercent
                                    && p.x >= topLineFinal.get(0).x - (double) topLineFinal.get(0).width / 100 * tolleranceInPercent
                                    && p.x + p.width <= topLineFinal.get(topLineFinal.size() - 1).x + topLineFinal.get(topLineFinal.size() - 1).width + (double) topLineFinal.get(0).width / 100 * tolleranceInPercent
                    )
                    .collect(Collectors.toList());
            newLine.sort(Comparator.comparingInt(p -> p.x));
            if (topLine.size() == newLine.size() && checkLineIntegrity(newLine)) {
                restLines.add(newLine);
                topLine = newLine;
            } else {
                return restLines;
            }
        }
    }

    private static boolean checkLineIntegrity(List<Rect> topLine) {

        for (int i = 1; i < topLine.size(); i++) {
            if (topLine.get(i).x < topLine.get(i - 1).x + topLine.get(i - 1).width) {
                return false;
            }
        }

        return true;
    }

}
