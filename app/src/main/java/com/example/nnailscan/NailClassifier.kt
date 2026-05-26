package com.example.nnailscan

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Clasificador on-device (EfficientNetB1 exportado desde entrenar_modelo.py).
 *
 * Assets requeridos:
 * - nail_model_nuevo.tflite
 * - labels.txt
 *
 * Preprocesado alineado con entrenar_modelo.py (EfficientNet Keras 3):
 *   float32 RGB en rango 0–255 (la normalización va dentro del grafo TFLite).
 */
class NailClassifier(
    context: Context,
    private val modelAssetName: String = "nail_model_nuevo.tflite",
    private val labelsAssetName: String = "labels.txt",
) : AutoCloseable {

    private val interpreter: Interpreter
    private val labels: List<String>
    private val inputWidth: Int
    private val inputHeight: Int
    private val numClasses: Int

    init {
        val modelBuffer = loadModelFile(context, modelAssetName)
        interpreter = Interpreter(modelBuffer, Interpreter.Options().apply {
            setNumThreads(4)
        })
        labels = loadLabels(context, labelsAssetName)

        val inputShape = interpreter.getInputTensor(0).shape()
        inputHeight = inputShape[1]
        inputWidth = inputShape[2]
        numClasses = interpreter.getOutputTensor(0).shape()[1]

        require(labels.size == numClasses) {
            "labels.txt tiene ${labels.size} clases pero el modelo espera $numClasses"
        }
    }

    fun classifyImage(bitmap: Bitmap): Pair<String, Float> {
        val resized = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true)
        val inputBuffer = bitmapToByteBuffer(resized)
        val output = Array(1) { FloatArray(numClasses) }

        interpreter.run(inputBuffer, output)

        val probabilities = output[0]
        var bestIndex = 0
        var bestProbability = probabilities[0]
        for (i in 1 until numClasses) {
            if (probabilities[i] > bestProbability) {
                bestProbability = probabilities[i]
                bestIndex = i
            }
        }

        return labels[bestIndex] to (bestProbability * 100f)
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * inputWidth * inputHeight * 3)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputWidth * inputHeight)
        bitmap.getPixels(pixels, 0, inputWidth, 0, 0, inputWidth, inputHeight)

        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16) and 0xFF).toFloat())
            buffer.putFloat(((pixel shr 8) and 0xFF).toFloat())
            buffer.putFloat((pixel and 0xFF).toFloat())
        }
        buffer.rewind()
        return buffer
    }

    override fun close() {
        interpreter.close()
    }

    private fun loadLabels(context: Context, assetName: String): List<String> =
        context.assets.open(assetName).bufferedReader().use { reader ->
            reader.readLines().map { it.trim() }.filter { it.isNotEmpty() }
        }

    companion object {
        private fun loadModelFile(context: Context, assetName: String): MappedByteBuffer {
            context.assets.openFd(assetName).use { assetFd ->
                FileInputStream(assetFd.fileDescriptor).use { input ->
                    val channel = input.channel
                    return channel.map(
                        FileChannel.MapMode.READ_ONLY,
                        assetFd.startOffset,
                        assetFd.declaredLength,
                    )
                }
            }
        }
    }
}
