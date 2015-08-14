package me.scai.plato.mathjax.webapp.servlets.conversions;


import org.apache.commons.codec.binary.Base64;

import java.io.IOException;

public class ConversionToImagePng extends ConversionToImage {
    /* Constructors */
    public ConversionToImagePng(String executablePath, int imageWidth, int imageDpi) {
        super(executablePath, imageWidth, imageDpi);
    }

    /* Methods */
    public String convert(String mathTex) throws IOException {
        return Base64.encodeBase64String(convertToByteArray(mathTex));
    }
}
