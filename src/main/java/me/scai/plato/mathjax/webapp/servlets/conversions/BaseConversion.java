package me.scai.plato.mathjax.webapp.servlets.conversions;


import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class BaseConversion implements MathJaxConversion {
    /* Member variables */
    protected String executablePath;

    public BaseConversion(String executablePath) {
        this.executablePath = executablePath;

        if (executablePath == null || executablePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid executable path: " + executablePath);
        }
    }

    protected String wrapMathTex(String mathTex) {
        String wrappedMathTex = "{" + mathTex + "}";

        return wrappedMathTex;
    }

    protected byte[] getByteArray(String[] cmd) throws IOException {
        Process proc = Runtime.getRuntime().exec(cmd);

        /* Capture stderr */
        InputStream stderr = proc.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder err = new StringBuilder("");
        String errLine = null;
        while ( (errLine = br.readLine()) != null) {
            err.append(errLine + "\n");
        }

        if (err.length() > 0) {
            throw new RuntimeException("Execution of mathjax binary " + executablePath  + "  failed due to: " + err.toString());
        }

        /* Capture stdout */
        String output = null;
        InputStream stdoutStream = proc.getInputStream();

        byte[] dataBytes = IOUtils.toByteArray(stdoutStream);

        return dataBytes;
    }


}
