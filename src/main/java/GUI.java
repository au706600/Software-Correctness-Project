import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;

//------------------------------------------------------------

public class GUI {
    JFrame frame;
    JPanel panel;
    JTextArea codeTextArea;
    JTextArea errorTextArea;
    int pixelSize;
    int w;
    int h;
    int x;
    int y;
    int newStartX = 0; // After dragging, start at valid x
    int newStartY = 0; // After dragging, start at valid y
    int offsetX = 0;
    int offsetY = 0;
    double zoom = 1.0;
    //public Point origin = new Point(0,0);
    ArrayList<ArrayList<Integer>> pixels;
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
                pixels = result.getPixels();
                panel.repaint();
                /*
                var graphics = panel.getGraphics();
                for(int i=0; i<w; i++) {
                    for(int j=0; j<h; j++) {
                        //System.out.println(result.getPixels().size());
                        if(result.getPixels().size()>i && result.getPixels().get(i).size()>j) {
                            graphics.setColor(Color.BLACK);
                        } else {
                            graphics.setColor(Color.WHITE);
                        }
                        graphics.fillRect(i*pixelSize, (h-j)*pixelSize, pixelSize, pixelSize);
                    }
                }
                    */
            }
        });

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if(pixels == null)
                {
                    return;
                }

                for(int i = 0; i < pixels.size(); i++)
                {
                    for(int j = 0; j < pixels.get(i).size(); j++)
                    {
                        int v = pixels.get(i).get(j);
                        switch (v) {
                            case 1 -> g.setColor(Color.BLACK);    
                            case 2 -> g.setColor(Color.LIGHT_GRAY);     
                            default -> g.setColor(Color.WHITE);   
                        }
                        
                        g.fillRect(
                            (int)(i * pixelSize * zoom + offsetX), 
                            (int)(j * pixelSize * zoom + offsetY), 
                            (int)(pixelSize * zoom), 
                            (int)(pixelSize * zoom)
                        );
                    }
                }

                g.setColor(Color.LIGHT_GRAY);

                int zoomGraphic = (int)(pixelSize * zoom);

                newStartX = offsetX % zoomGraphic;
                newStartY = offsetY % zoomGraphic;

                if(newStartX < 0)
                {
                    newStartX += zoomGraphic;
                }

                if(newStartY < 0)
                {
                    newStartY += zoomGraphic;
                }


                
                for(int i = newStartX; i < getWidth(); i+=zoomGraphic)
                {
                    g.drawLine(i, 0, i, getHeight());
                }

                for(int j = newStartY; j < getHeight(); j+=zoomGraphic)
                {
                    g.drawLine(0, j, getWidth(), j);
                }
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                x = e.getX();
                y = e.getY();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                int dx = e.getX() - x;
                int dy = e.getY() - y;

                offsetX += dx;
                offsetY += dy;

                x = e.getX();
                y = e.getY();

                panel.repaint();
            }
        });

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override 
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                if(e.getWheelRotation() < 0)
                {
                    zoom *= 1.1;
                }

                else
                {
                    zoom /= 1.1;
                }

                panel.repaint();
            }
        });
        


        //------------------------------------------------------------------------

        errorTextArea.setText("Some error code");
        SwingUtilities.invokeLater(() -> {

            JSplitPane verticalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                panel, 
                errorTextArea
            );

            verticalSplit.setDividerLocation(750);

            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.add(verticalSplit, BorderLayout.CENTER);


            //leftPanel.setLayout(new GridLayout(2, 1));
            //frame.setLayout(new GridLayout(1,2));

            JSplitPane horizontalSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, 
                leftPanel, 
                codeTextArea
            );

            horizontalSplit.setDividerLocation(700);
            verticalSplit.setDividerSize(2);
            horizontalSplit.setDividerSize(2);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));


            frame.setSize(1000, 1000);
            frame.setTitle("GUI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //leftPanel.add(panel);
            frame.add(horizontalSplit);
            //frame.add(codeTextArea);
            //leftPanel.add(errorTextArea);
            frame.setVisible(true);
        });
    }
    
}
