import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {

    public Mat processImage(Mat src) {
        Mat dest = src.clone();
        Imgproc.cvtColor(src, dest, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(dest, dest, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(dest, dest, 255, 1, 1, 11, 2);

        dest = findContourss(dest);

        return dest;
    }

    public static Mat findContourss(Mat src){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat result = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        for (int i = 0; i < contours.size(); i++) {
            List<Point> points = contours.get(i).toList();
            //Rect rect = Imgproc.boundingRect(contours.get(i));
            for (Point point : points) {
                Imgproc.circle(result, point, 1, new Scalar(255,10,15));
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
