import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.util.function.Function;

public class GroupingBy {

    public static  <T> Map<Integer, List<T>> approximate(List<T> list, Function<T, Integer> function, int tolleranceInPercents) {

        Map<Integer, List<T>> map = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {

            Integer closestValue = list.stream()
                    .map(function)
                    .min(Comparator.comparingInt(p->p))
                    .orElse(-1);

            if (closestValue > 0 && closestValue / 100 * tolleranceInPercents >= Math.abs(closestValue - function.apply(list.get(i)))) {
                map.get(closestValue).add(list.get(i));
            } else {
                List<T> mapValueList = new ArrayList<>();
                mapValueList.add(list.get(i));
                map.put(function.apply(list.get(i)), mapValueList);
            }

        }
        return map;
    }
}
