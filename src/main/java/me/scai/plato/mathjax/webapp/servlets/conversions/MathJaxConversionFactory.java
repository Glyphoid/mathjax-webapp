package me.scai.plato.mathjax.webapp.servlets.conversions;


import com.google.gson.JsonObject;
import me.scai.plato.mathjax.webapp.servlets.conversions.ConversionToImagePng;
import me.scai.plato.mathjax.webapp.servlets.conversions.ConversionToStringMathML;
import me.scai.plato.mathjax.webapp.servlets.conversions.MathJaxConversion;

import java.util.HashMap;
import java.util.Map;

public class MathJaxConversionFactory {
    /* Constants */
    private static final String IMAGE_WIDTH_FIELD = "imageWidth";
    private static final String IMAGE_DPI_FIELD = "imageDpi";

    private static final String FORMAT_IMAGE_PNG = "png";
    private static final String FORMAT_STRING_MATHML = "MathML";

    /* Member variables */
    private Map<String, String> executablePaths = new HashMap<String, String>();

    /* Constructors */
    public MathJaxConversionFactory() {

    }

    /* Methods */
    public void addConversion(String format, String executablePath) {
        executablePaths.put(format.toLowerCase(), executablePath);
    }


    public MathJaxConversion getConversion(String format, JsonObject auxData) {
        if ( !executablePaths.containsKey(format) ) {
            throw new IllegalArgumentException("Unsupported conversion format: " + format);
        }

        String executablePath = executablePaths.get(format);

        if ( format.equalsIgnoreCase(FORMAT_IMAGE_PNG) ) {
            return new ConversionToImagePng(
                    executablePath,
                    auxData.get(IMAGE_WIDTH_FIELD).getAsInt(),
                    auxData.get(IMAGE_DPI_FIELD).getAsInt()
            );
        } else if (format.equalsIgnoreCase(FORMAT_STRING_MATHML)) {
            return new ConversionToStringMathML(executablePath);

        } else {
            throw new IllegalArgumentException("Unsupported conversion format: " + format);
        }
    }

    public static String getImageWidthField() {
        return IMAGE_WIDTH_FIELD;
    }

    public static String getImageDpiField() {
        return IMAGE_DPI_FIELD;
    }

    public static String getFormatImagePng() {
        return FORMAT_IMAGE_PNG;
    }

    public static String getFormatStringMathML() {
        return FORMAT_STRING_MATHML;
    }
}
