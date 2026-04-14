import java.util.ArrayList;

record JavaPixel(int x, int y, java.awt.Color color) {}
record JavaPositionText(int x, int y, java.awt.Color color, String text, int size) {}
record ResultFromScala(ArrayList<JavaPixel> pixels, ArrayList<JavaPositionText> text, String error) {}

