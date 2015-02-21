import java.io.IOException;
import java.util.regex.Matcher;

//Speeds up generation of large images (and small ones, but that's not as noticeable)
public class FractalThread implements Runnable {

	private int n;
	private int startingPoint;
	private int endPoint;
	private int[] rgbArray;

	public FractalThread(int n, int startingPoint, int endPoint, int[] rgbArray) {
		this.n = n;
		this.startingPoint = startingPoint;
		this.endPoint = endPoint;
		this.rgbArray = rgbArray;
	}

	@Override
	public void run() {
		for (int i = startingPoint; i < endPoint; i++) {
			String code = getCode(i);
			Matcher m = Fractal.regex.matcher(code);
			float percentage;
			int rgb = 0;
			if (m.find()) {
				String match = m.group(1);
				percentage = match.length()/(float)code.length();
			} else {
				percentage = 1;
				rgb += 255;
			}
			rgb += (int) (255 * percentage) << 16 + (int) (255 * percentage) << 8;
			rgbArray[i] = rgb;
		}
		try {
			Fractal.writeImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getCode(int pixel) {
		StringBuilder code = new StringBuilder();
		int currentN = n;
		int x = pixel % currentN;
		int y = pixel / currentN;
		while (currentN > 1) {
			if (y >= currentN >> 1) {
				if (x >= currentN >> 1) {
					code.append("4");
					x -= currentN >> 1;
					y -= currentN >> 1;
				} else {
					code.append("3");
					y -= currentN >> 1;
				}
			} else {
				if (x >= currentN >> 1) {
					code.append("1");
					x -= currentN >> 1;
				} else {
					code.append("2");
				}
			}
			currentN = currentN >> 1;
		}
		return code.toString();
	}

}
