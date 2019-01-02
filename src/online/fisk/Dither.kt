package online.fisk

import online.fisk.filters.Filter
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    if(args.isEmpty() || args[0] == "help" || args.size == 1){
        Dither().showHelp()
    }else {
        val threshold = when {
            args.size >= 3 -> args[2].toInt()
            else -> 128
        }
        Dither().process(args[0], args[1], threshold)
    }
}

class Dither{

    companion object {
        fun out(message: String){
            System.out.println(message)
        }
    }

    fun showHelp(){
        out("DitherCL - Help")
        out("Usage: DitherCL pathToImage filterName")
        out("Available filters:")
        Filter.availableFilterLabels().forEach(::out)
    }

    fun process(source: String, filterName: String, threshold: Int){
        out("DitherCL - processing...")

        //Invalid source file string will exit
        val file = File(source)
        validateFile(source, file)
        validateFilter(filterName)

        val sourceImage: BufferedImage = ImageIO.read(file)
        validateImage(sourceImage)

        val processed = Filter.get(filterName)
            .threshold(threshold)
            .process(sourceImage)

        val exportFilename = "${file.nameWithoutExtension}_$filterName.png"
        val exportFile = File(exportFilename)

        ImageIO.write(processed, "png", exportFile)

        out("Processed image: $exportFilename")

        System.exit(0)
    }

    private fun validateFile(source: String, file: File){
        if(!file.exists()){
            out("$source does not exist")
            System.exit(-1)
        }else{
            out("Source: $source")
        }
    }

    private fun validateFilter(filterName: String){
        if(!Filter.availableFilterLabels().contains(filterName)){
            out("$filterName is not a valid filter")
            out("Available filters:")
            Filter.availableFilterLabels().forEach(::out)
            System.exit(-1)
        }else{
            out("Filter: $filterName")
        }
    }

    private fun validateImage(image: BufferedImage?){
        when (image) {
            null -> {
                out("File is not a valid image")
                System.exit(-1)
            }
        }
    }
}