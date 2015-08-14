package me.scai.plato.mathjax.webapp.servlets.conversions;

import java.io.IOException;

public abstract class ConversionToImage extends BaseConversion {
    /* Constants */
    private static final String WIDTH_OPTION_FLAG = "--width";
    private static final String DPI_OPTION_FLAG   = "--dpi";

    /* Member variables */
    private int imageWidth;
    private int imageDpi;

    /* Constructors */
    public ConversionToImage(String executablePath, int imageWidth, int imageDpi) {
        super(executablePath);

        if (imageWidth <= 0) {
            throw new IllegalArgumentException("Invalid image width: " + imageWidth);
        }
        if (imageDpi <= 0) {
            throw new IllegalArgumentException("Invalid image dpi: " + imageDpi);
        }

        this.imageWidth = imageWidth;
        this.imageDpi   = imageDpi;
    }

    protected byte[] convertToByteArray(String mathTex) throws IOException {
        String[] cmd = {
            executablePath,
            WIDTH_OPTION_FLAG,
            Integer.toString(imageWidth),
            DPI_OPTION_FLAG,
            Integer.toString(imageDpi),
            wrapMathTex(mathTex)
        };

        return getByteArray(cmd);
    }


}
