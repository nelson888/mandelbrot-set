package com.tambapps.mandelbrot

import java.util.concurrent.Executors

def executor = Executors.newFixedThreadPool(Runtime.runtime.availableProcessors() + 1)
new MandelbrotImage(executor, 100, 100, 300, 200, 255).setVisible(true)
