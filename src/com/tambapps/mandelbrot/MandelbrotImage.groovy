package com.tambapps.mandelbrot

import javax.swing.JFrame
import java.awt.Graphics
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.image.BufferedImage
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService

class MandelbrotImage extends JFrame {

    private final MandelbrotComputer compute
    private final BufferedImage image
    private final ExecutorCompletionService executorService
    private final int maxLineUpdate
    private double cX = 0.0
    private double cY = 0.0
    private double aX
    private double aY
    private double scale = 0.02

    MandelbrotImage(ExecutorService executor,
                    int x, int y, int width, int height, int maxIteration) {
        super("Mandelbrot set")
        setBounds(x, y, width, height)
        executorService = new ExecutorCompletionService(executor)
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
        compute = new MandelbrotComputer(maxIteration)
        maxLineUpdate = height < 100 ? 1 : height / 100
        def mouseEventListener = new MouseEventListener()
        addMouseListener(mouseEventListener)
        addMouseMotionListener(mouseEventListener)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        updateAll()
    }

    void updateAll() {
        update { int x, int y, int halfWidth, int halfHeight ->
            updatePixel(x, y, halfWidth, halfHeight)
        }
    }

    void update(Closure process) {
        int height = getHeight()
        int width = getWidth()
        int halfWidth = width >> 1 //divide by 2
        int halfHeight = height >> 1
        for (int y = - halfHeight; y < halfHeight; y++) {
            for (int x = - halfWidth; x < halfWidth; x++) {
                process(x, y, halfWidth, halfHeight)
            }
        }
    }

    void updatePixel(int x, int y, int halfWidth, int halfHeight) {
        aX = cX + x * scale
        aY = cY + y * scale
        image.setRGB(x + halfWidth, y + halfHeight, compute(aX, aY))
    }

    void asyncUpdateAndRender() {
        update { int x, int y, int halfWidth, int halfHeight ->
            executorService.submit({
                updatePixel(x, y, halfWidth, halfHeight)
            })
        }

        int linesRemaining = getHeight()
        while (linesRemaining > 0) {
            int linesTaken = Math.min(maxLineUpdate, linesRemaining)
            for (int i = 0; i < getWidth() * linesTaken; i++) {
                executorService.take()
            }
            linesRemaining -= linesTaken
            repaintAll()
        }
    }

    @Override
    void paint(Graphics graphics) {
        graphics.drawImage(image, 0, 0, this)
    }

    void zoomImage(Point point) {
        cX += scale * (point.x - getWidth() / 2)
        cY += scale * (point.y - getHeight() / 2)
        scale *= 0.5
        asyncUpdateAndRender()
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
