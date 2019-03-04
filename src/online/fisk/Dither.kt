package online.fisk

import online.fisk.filters.Filter
import online.fisk.filters.FilterImage
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

        //Invalid source file or filter name will quit the process:
        val file = File(source)
        validateFile(source, file)
        validateFilter(filterName)

        val sourceImage: BufferedImage = ImageIO.read(file)

        //Null image check:
        validateImage(sourceImage)

        val destination = BufferedImage(sourceImage.width, sourceImage.height, BufferedImage.TYPE_INT_RGB)
        val destinationImpl = FilterImageImpl(destination)

        Filter.get(filterName)
            .threshold(threshold)
            .process(FilterImageImpl(sourceImage), destinationImpl) {
                val exportFilename = "${file.nameWithoutExtension}_$filterName.png"
                val exportFile = File(exportFilename)

                ImageIO.write(destinationImpl.image, "png", exportFile)

                out("Processed image: $exportFilename")

                System.exit(0)
            }
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


    class FilterImageImpl(val image: BufferedImage): FilterImage() {

        override var width: Int
            get() = image.width

            @Suppress("UNUSED_PARAMETER")
            set(value) {
                //unused'
            }
        
        override var height: Int
            get() = image.height

            @Suppress("UNUSED_PARAMETER")
            set(value) {
                //unused
            }

        override fun getPixel(x: Int, y: Int): Int {
            return image.getRGB(x, y)
        }

        override fun setPixel(x: Int, y: Int, colour: Int) {
            image.setRGB(x, y, colour)
        }
    }
}