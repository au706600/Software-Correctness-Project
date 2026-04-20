import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
    ArrayList<JavaPixel> pixels;
    ArrayList<JavaPositionText> positionTexts;
    
    void update() {
        ResultFromScala result = converter$.MODULE$.convert(codeTextArea.getText());
        errorTextArea.setText(result.error());
        pixels = result.pixels();
        positionTexts = result.text();
        panel.repaint();
    };

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
                update();
            }
        });

        panel = new JPanel() {
            int xPosToCanvasPos(int pos) {
                return (int)(pos * pixelSize * zoom + offsetX);
            }
            
            int yPosToCanvasPos(int pos) {
                return getHeight() - (int)(pos * pixelSize * zoom - offsetY);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if(pixels == null)
                {
                    return;
                }

                for(JavaPixel pixel : pixels)
                {
                    g.setColor(pixel.color());
                    
                    g.fillRect(
                        xPosToCanvasPos(pixel.x()),
                        yPosToCanvasPos(pixel.y()),
                        (int)(pixelSize * zoom + 1), 
                        (int)(pixelSize * zoom + 1));
                }
                

                if(zoom > 5) {
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
                    
                    for(int i = newStartX; i < getWidth(); i += zoomGraphic)
                    {
                        g.drawLine(i, 0, i, getHeight());
                    }
    
                    for(int j = newStartY; j < getHeight(); j += zoomGraphic)
                    {
                        g.drawLine(0, j, getWidth(), j);
                    }
                }

                
                for(JavaPositionText positionText : positionTexts)
                    {
                    g.setFont(new Font("serif", Font.PLAIN, (int)(positionText.size()*zoom)));
                    g.setColor(positionText.color());
                    g.drawString(
                        positionText.text(),
                        xPosToCanvasPos(positionText.x()),
                        yPosToCanvasPos(positionText.y()));
                }

                g.setColor(Color.BLACK);
                g.drawLine(0, getHeight() + (int)offsetY, getWidth(), getHeight() + (int)offsetY);
                g.drawLine((int)offsetX, 0, (int)offsetX, getHeight());
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

 

        String startOfTemplate;
        Path startOfTemplatePath = Path.of("startOfTemplate.clj");
        try {
            startOfTemplate = Files.readString(startOfTemplatePath);
        } catch (Exception e) {
            startOfTemplate = "";
        }
        codeTextArea.setText(startOfTemplate);
        update();
        
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
            frame.setTitle("DrawGebra");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //leftPanel.add(panel);
            frame.add(horizontalSplit);
            //frame.add(codeTextArea);
            //leftPanel.add(errorTextArea);
            frame.setVisible(true);
        });
    }
    
}
