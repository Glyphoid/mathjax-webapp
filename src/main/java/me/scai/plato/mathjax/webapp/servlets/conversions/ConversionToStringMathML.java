package me.scai.plato.mathjax.webapp.servlets.conversions;


import java.io.IOException;

public class ConversionToStringMathML extends ConversionToString {
    final String defaultMathMLEncoding = "utf-8";

    /* Constructor */
    public ConversionToStringMathML(String executablePath) {
        super(executablePath);
    }

    /* Methods */
    public String convert(String mathTex) throws IOException {
        String[] cmd = {
            executablePath,
            wrapMathTex(mathTex)
        };

        byte[] data = getByteArray(cmd);
        return new String(data, defaultMathMLEncoding);
    }
}


