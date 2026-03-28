import java.util.ArrayList;

record JavaPixel(int x, int y, java.awt.Color color) {}
record ResultFromScala(ArrayList<JavaPixel> pixels, String error) {}

