package me.scai.plato.mathjax.webapp.servlets.conversions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestConversionToImagePng {
    private static String defaultTex2pngExecutablePath = "/local/programs/node_modules/MathJax-node/bin/tex2png";

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testConversionToPng() {
        final String tex2pngExecutablePath = defaultTex2pngExecutablePath; // TODO: Do not hard code path for test
        final int imageWidth = 100;
        final int imageDpi = 200;

        final String mathTex = "\\frac{1}{3}";

        MathJaxConversion conversion = new ConversionToImagePng(tex2pngExecutablePath, imageWidth, imageDpi);

        String converted = null;
        try {
            converted = conversion.convert(mathTex);
        } catch (IOException ioExc) {
            fail("Failed due to IOException: " + ioExc.getMessage());
        }

        assertNotNull(converted);
        assertFalse(converted.isEmpty());
    }

    @Test
    public void testInvalidExecutablePath() {
        final String tex2pngExecutablePath = "";
        final int imageWidth = 100;
        final int imageDpi = 200;

        boolean exceptionThrown = false;
        try {
            MathJaxConversion conversion = new ConversionToImagePng(tex2pngExecutablePath, imageWidth, imageDpi);
        } catch (IllegalArgumentException iaExc) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    public void testInvalidImageWidth() {
        final String tex2pngExecutablePath = defaultTex2pngExecutablePath;
        final int imageWidth = -100;
        final int imageDpi = 200;

        boolean exceptionThrown = false;
        try {
            MathJaxConversion conversion = new ConversionToImagePng(tex2pngExecutablePath, imageWidth, imageDpi);
        } catch (IllegalArgumentException iaExc) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    public void testInvalidImageDpi() {
        final String tex2pngExecutablePath = defaultTex2pngExecutablePath;
        final int imageWidth = 100;
        final int imageDpi = -200;

        boolean exceptionThrown = false;
        try {
            MathJaxConversion conversion = new ConversionToImagePng(tex2pngExecutablePath, imageWidth, imageDpi);
        } catch (IllegalArgumentException iaExc) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }



}
