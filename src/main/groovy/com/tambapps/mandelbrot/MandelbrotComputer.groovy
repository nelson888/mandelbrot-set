package com.tambapps.mandelbrot

class MandelbrotComputer extends Closure<Integer> {

    private final int maxIteration

    MandelbrotComputer(int maxIteration) {
        super(null)
        this.maxIteration = maxIteration
    }

    Integer doCall(double cX, double cY) {
        double zx
        double zy
        double tmp
        zx = zy = 0
        int iter = maxIteration
        while (zx * zx + zy * zy < 4 && iter > 0) {
            tmp = zx * zx - zy * zy + cX
            zy = 2.0 * zx * zy + cY
            zx = tmp
            iter--
        }
        return iter | iter << 8
    }

}
