package me.scai.plato.mathjax.webapp.servlets.conversions;

import java.io.IOException;

public interface MathJaxConversion {
    String convert(String mathTex) throws IOException;
}
