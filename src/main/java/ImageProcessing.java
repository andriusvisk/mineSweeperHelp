import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.*;
import java.util.stream.Collectors;

public class ImageProcessing {

    public Mat processImage(Mat src) {
        Mat dest = src.clone();
        Imgproc.cvtColor(src, dest, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(dest, dest, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(dest, dest, 255, 1, 1, 11, 2);

        dest = findContours(dest);

        return dest;
    }

    public static Mat findContours(Mat src) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Map<Integer, List<Rect>> boundingRectByArea = new HashMap<>();

        Mat result = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        for (int i = 0; i < contours.size(); i++) {

            Rect boundingRect = Imgproc.boundingRect(contours.get(i));
            Integer area = (int) boundingRect.area();

            if (area > 20) {

                int closestArea = boundingRectByArea.keySet().stream()
                        .min(Comparator.comparingInt(p -> Math.abs(p - area)))
                        .orElse(-1);

                int tolleranceInPercents = 15;

                if (closestArea > 0 && closestArea / 100 * tolleranceInPercents >= Math.abs(closestArea - area)) {
                    boundingRectByArea.get(closestArea).add(boundingRect);
                } else {
                    List<Rect> list = new ArrayList<>();
                    list.add(boundingRect);
                    boundingRectByArea.put(area, list);
                }

            }
        }

        boundingRectByArea = boundingRectByArea.entrySet().stream()
                .filter(i -> i.getValue().size() >= 4)
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

        Map<Integer, List<Rect>> boundingRectByLines = new HashMap<>();

        int tolleranceInPercents = 15;

        for (Integer key : boundingRectByArea.keySet()) {
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

    public static Image mat2Image(Mat src) {
        return convertToFxImage(matToBufferedImage(src));
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        return new ImageView(wr).getImage();
    }

    private static BufferedImage matToBufferedImage(Mat original) {
        // init
        BufferedImage image = null;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }
}
