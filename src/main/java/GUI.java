import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.*;


public class GUI {
    JFrame frame;
    JPanel panel;
    JTextArea codeTextArea;
    JTextArea errorTextArea;
    int pixelSize;
    int w;
    int h;
    static public void javaFun1() {
        System.out.println("javaFun1 called");
    }

    public GUI(int w_, int h_, int pixelSize_) {
        pixelSize = pixelSize_;
        w = w_;
        h = h_;
        frame = new JFrame();
        panel = new JPanel();
        codeTextArea = new JTextArea();
        errorTextArea = new JTextArea();
        codeTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                ResultFromScala result = converter$.MODULE$.convert(codeTextArea.getText());
                errorTextArea.setText(result.getError());
                var graphics = panel.getGraphics();
                for(int i=0; i<w; i++) {
                    for(int j=0; j<h; j++) {
                        System.out.println(result.getPixels().size());
                        if(result.getPixels().size()>i && result.getPixels().get(i).size()>j) {
                            graphics.setColor(Color.BLACK);
                        } else {
                            graphics.setColor(Color.WHITE);
                        }
                        graphics.fillRect(i*pixelSize, (h-j)*pixelSize, pixelSize, pixelSize);
                    }
                }
            }
        });
        errorTextArea.setText("Some error code");
        SwingUtilities.invokeLater(() -> {
            frame.setLayout(new GridLayout(2, 2));
            frame.setSize(1000, 1000);
            frame.setTitle("GUI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.add(codeTextArea);
            frame.add(errorTextArea);
            frame.setVisible(true);
        });
    }
    
}
