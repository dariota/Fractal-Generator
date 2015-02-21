import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

//Generates fractals from regex, uses a LOT of memory for large images
public class Fractal {

	public static int imageSide;
	public static BufferedImage fractal;
	public static Pattern regex;
	public static int threadNumber;
	private static int returnedThreads = 0;
	private static long t = System.nanoTime();
	public static int[] rgbArray;

	public static void main(String[] args) throws IOException {
		imageSide = Integer.parseInt(args[0]);
		regex = Pattern.compile(args[1]);
		threadNumber = Runtime.getRuntime().availableProcessors();
		int pixelCount = imageSide * imageSide;
		rgbArray = new int[pixelCount];
		int section = (pixelCount) / threadNumber;
		for (int i = 0; i < threadNumber; i++) {
			System.out.println("Starting thread " + i);
			Thread thread = new Thread(new FractalThread(imageSide, section * i, section * (i + 1), rgbArray));
			thread.start();
		}
		fractal = new BufferedImage(imageSide, imageSide, BufferedImage.TYPE_INT_RGB);
	}

	public static synchronized void writeImage() throws IOException {
		returnedThreads++;
		if(returnedThreads >= threadNumber) {
			fractal.setRGB(0, 0, imageSide, imageSide, rgbArray, 0, imageSide);
			File f = new File("fractal.png");
			ImageIO.write(fractal, "PNG", f);
			System.out.println("Done in " + (System.nanoTime()-t)/1000000000F + " s");
		}
	}

}