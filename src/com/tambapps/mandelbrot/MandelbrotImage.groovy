package com.tambapps.mandelbrot

import javax.swing.JFrame
import java.awt.BasicStroke
import java.awt.Graphics
import java.awt.Point
import java.awt.Stroke
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.image.BufferedImage

class MandelbrotImage extends JFrame {

    private final MandelbrotComputer compute
    private final MouseEventListener mouseEventListener
    private final BufferedImage image
    private final Stroke stroke
    private double cX = 0.0
    private double cY = 0.0
    private double aX
    private double aY
    private double scale = 0.02

    MandelbrotImage(int x, int y, int width, int height, int maxIteration) {
        super("Mandelbrot set")
        setBounds(x, y, width, height)
        stroke = new BasicStroke(Math.min(width, height) / 100)
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
        compute = new MandelbrotComputer(maxIteration)
        mouseEventListener = new MouseEventListener()
        addMouseListener(mouseEventListener)
        addMouseMotionListener(mouseEventListener)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        update()
    }

    void update() {
        int height = getHeight()
        int width = getWidth()
        int halfWidth = width >> 1 //divide by 2
        int halfHeight = height >> 1
        for (int y = - halfHeight; y < halfHeight; y++) {
            for (int x = - halfWidth; x < halfWidth; x++) {
                aX = cX + x * scale
                aY = cY + y * scale
                image.setRGB(x + halfWidth, y + halfHeight, compute(aX, aY))
            }
        }
    }

    @Override
    void paint(Graphics graphics) {
        graphics.drawImage(image, 0, 0, this)
        graphics.setStroke(stroke) //defined in Graphics2D
    }

    void zoomImage(Point point) {
        cX += scale * (point.x - getWidth() / 2)
        cY += scale * (point.y - getHeight() / 2)
        scale *= 0.5
        update()
        repaintAll()
        println("scale: $scale")
        println("cX: $cX")
        println("cY: $cY")
        println()
    }

    private void repaintAll() {
        revalidate()
        repaint()
    }

    private class MouseEventListener implements MouseListener, MouseMotionListener {

        @Override
        void mouseClicked(MouseEvent mouseEvent) {
            if (isLeftButton(mouseEvent)) {
                zoomImage(mouseEvent.point)
            }
        }

        @Override
        void mousePressed(MouseEvent mouseEvent) {}

        @Override
        void mouseReleased(MouseEvent mouseEvent) {}

        @Override
        void mouseEntered(MouseEvent mouseEvent) {}

        @Override
        void mouseExited(MouseEvent mouseEvent) {}

        @Override
        void mouseDragged(MouseEvent mouseEvent) {}

        @Override
        void mouseMoved(MouseEvent mouseEvent) {}

        boolean isLeftButton(MouseEvent mouseEvent) {
            return mouseEvent.button == MouseEvent.BUTTON1
        }
    }
}
