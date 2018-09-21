package com.tambapps.mandelbrot

import javax.swing.JFrame
import java.awt.BasicStroke
import java.awt.Graphics
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.image.BufferedImage

class MandelbrotImage extends JFrame {

    private final Closure<Integer> compute
    private final MouseEventListener mouseEventListener
    private final BufferedImage image
    private double cX = 0.0
    private double cY = 0.0
    private double aX
    private double aY
    private double scale = 0.02

    MandelbrotImage(int x, int y, int width, int height, int maxIteration) {
        super("Mandelbrot set")
        setBounds(x, y, width, height)
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
        compute = new MandelbrotComputer(maxIteration).memoize()
        mouseEventListener = new MouseEventListener()
        addMouseListener(mouseEventListener)
        addMouseMotionListener(mouseEventListener)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        update()
    }

    void update() {
        int height = getHeight()
        int width = getWidth()
        int halfWidth = width >> 1
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
        graphics.setStroke(new BasicStroke(getHeight()/100)) //defined in Graphics2D
        mouseEventListener.drawRectangle(graphics)
    }

    void reboundImage(int x, int y) {
        cX += scale * (x - getWidth()/2)
        cY += scale * (y - getHeight()/2)
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
        private static final int MIN_SIZE = 2
        private boolean pressing
        private Point downPoint
        private Point upPoint

        @Override
        void mouseClicked(MouseEvent mouseEvent) {

        }

        @Override
        void mousePressed(MouseEvent mouseEvent) {
            if (isLeftButton(mouseEvent)) {
                pressing = true
                downPoint = boundedPoint(mouseEvent.getPoint())
            }
        }

        @Override
        void mouseReleased(MouseEvent mouseEvent) {
            if (isLeftButton(mouseEvent)) {
                pressing = false
                reboundImage((int) ((downPoint.x + upPoint.x) / 2), (int) ((downPoint.y + upPoint.y) / 2))
            }
            repaintAll()
        }

        @Override
        void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        void mouseExited(MouseEvent mouseEvent) {

        }

        @Override
        void mouseDragged(MouseEvent mouseEvent) {
            if (pressing) {
                upPoint = boundedPoint(mouseEvent.getPoint())
                repaintAll()
            }
        }

        @Override
        void mouseMoved(MouseEvent mouseEvent) {

        }

        boolean isLeftButton(MouseEvent mouseEvent) {
            return mouseEvent.button == MouseEvent.BUTTON1
        }

        Point boundedPoint(Point input) {
            Point p = new Point()
            p.x = boundedValue(input.x, getWidth())
            p.y = boundedValue(input.y, getHeight())
            return p
        }

        private int boundedValue(double current, int max) {
            return Math.max(Math.min(max, (int)current), 0)
        }

        void drawRectangle(Graphics graphics) {
            if (!pressing) return
            int x = (int) Math.min(downPoint.x, upPoint.x)
            int y = (int) Math.min(downPoint.y, upPoint.y)
            int width = (int) Math.abs(downPoint.x - upPoint.x)
            int height = (int) Math.abs(downPoint.y - upPoint.y)
            if (width > MIN_SIZE && height > MIN_SIZE) {
                graphics.drawRect(x, y, width, height)
            }
        }
    }
}
