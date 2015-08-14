package me.scai.plato.mathjax.webapp.servlets.conversions;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestConversionToStringMathML {
    private static final String defaultTex2mmlExecutablePath = "/local/programs/node_modules/MathJax-node/bin/tex2png";

    @Test
    public void testConversionToMathML() {
        final String tex2mmlExectuablePath = defaultTex2mmlExecutablePath;

        final String mathTex = "\\frac{1}{3}";

        MathJaxConversion conversion = new ConversionToStringMathML(tex2mmlExectuablePath);

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
        final String tex2mmlExecutablePath = "";

        boolean exceptionThrown = false;
        try {
            MathJaxConversion conversion = new ConversionToStringMathML(tex2mmlExecutablePath);
        } catch (IllegalArgumentException iaExc) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }
}
