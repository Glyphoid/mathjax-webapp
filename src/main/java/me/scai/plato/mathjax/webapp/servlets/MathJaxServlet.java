package me.scai.plato.mathjax.webapp.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.scai.plato.mathjax.webapp.servlets.conversions.MathJaxConversion;
import me.scai.plato.mathjax.webapp.servlets.conversions.MathJaxConversionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Logger;

public class MathJaxServlet extends HttpServlet {
    /* Constants */
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();

    private static final String PROPERTIES_FILE_PATH = "/opt/mathjax-webapp/mathjax-webapp.properties";

    private static final Logger logger = Logger.getLogger(MathJaxServlet.class.getName());

    static final String MATH_TEX_FIELD = "mathTex";
    private static final String IMAGE_FORMAT_FIELD = "imageFormat";

    public static final String IMAGE_WIDTH_FIELD = MathJaxConversionFactory.getImageWidthField();
    public static final String IMAGE_DPI_FIELD = MathJaxConversionFactory.getImageDpiField();

    static final String CONVERSION_RESULT_FIELD = "conversionResult";
    static final String RESP_ERRORS_FIELD = "errors";

//    private static final String FORMAT_IMAGE_SVG = "svg";
    private static final String FORMAT_IMAGE_PNG     = MathJaxConversionFactory.getFormatImagePng();
    private static final String FORMAT_STRING_MATHML = MathJaxConversionFactory.getFormatStringMathML();


    /* Member variables */
    MathJaxConversionFactory conversionFactory;

//    private String tex2svgPath;
    private String tex2pngPath;
    private String tex2mmlPath;

    /* Exception classes */
    private final class RequestNotAJsonObjectException extends Exception {
        public RequestNotAJsonObjectException() {
            super();
        }

        public RequestNotAJsonObjectException(String msg) {
            super(msg);
        }
    }

//    private final class MathJaxExecutionException extends Exception {
//        public MathJaxExecutionException() {
//            super();
//        }
//
//        public MathJaxExecutionException(String msg) {
//            super(msg);
//        }
//    }

    private final class MissingMathTexException extends Exception {
        public MissingMathTexException() {
            super();
        }

        public MissingMathTexException(String msg) {
            super(msg);
        }
    }

//    private final class UnsupportedImageFormatException extends Exception {
//        public UnsupportedImageFormatException() {
//            super();
//        }
//
//        public UnsupportedImageFormatException(String msg) {
//            super(msg);
//        }
//    }

    private final class InvalidImageDimensionsException extends Exception {
        public InvalidImageDimensionsException() {
            super();
        }

        public InvalidImageDimensionsException(String msg) {
            super(msg);
        }
    }



    @Override
    public void init() throws ServletException {
        Properties props = new Properties();

        InputStream input = null;
        try {
            input = new FileInputStream(PROPERTIES_FILE_PATH);
            props.load(input);
        } catch (IOException exc) {
            logger.severe("Failed to load properties from file: " + exc.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        tex2svgPath = props.getProperty("tex2svgPath");
        tex2pngPath = props.getProperty("tex2pngPath");
        tex2mmlPath = props.getProperty("tex2mmlPath");

        if (tex2pngPath == null || tex2pngPath.isEmpty()) {
            throw new IllegalStateException("Cannot find the path to tex2png");
        }

        if (tex2mmlPath == null || tex2mmlPath.isEmpty()) {
            throw new IllegalStateException("Cannot find the path to tex2mml");
        }


//        logger.info("tex2svgPath = \"" + tex2svgPath + "\"");
        logger.info("tex2pngPath = \"" + tex2pngPath + "\"");
        logger.info("tex2mmlPath = \"" + tex2mmlPath + "\"");


        conversionFactory = new MathJaxConversionFactory();

        conversionFactory.addConversion(FORMAT_IMAGE_PNG, tex2pngPath);
        conversionFactory.addConversion(FORMAT_STRING_MATHML, tex2mmlPath);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {
        /* Read the body of the request */
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = request.getReader();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException exc) {

        } finally {
            try {
                reader.close();
            } catch (IOException e) {

            }
        }

        JsonArray errors = new JsonArray();

        String mathTex = null;
        String imageData = null;
        String imageFormatToGenerate = null;
        try {
            final String reqBody = sb.toString();

            JsonObject reqObj = null;

            try {
                reqObj = jsonParser.parse(reqBody).getAsJsonObject();
            } catch(Exception exc) {
                throw new RequestNotAJsonObjectException(exc.getMessage());
            }

            if (!reqObj.has(MATH_TEX_FIELD) ||
                !reqObj.get(MATH_TEX_FIELD).isJsonPrimitive()) {
                throw new MissingMathTexException();
            }
            mathTex = reqObj.get(MATH_TEX_FIELD).getAsString();
            imageFormatToGenerate = reqObj.get(IMAGE_FORMAT_FIELD).getAsString().toLowerCase();


            JsonObject auxData = new JsonObject();

            if (reqObj.has(IMAGE_WIDTH_FIELD) && reqObj.get(IMAGE_WIDTH_FIELD).isJsonPrimitive()) {
                auxData.add(IMAGE_WIDTH_FIELD, reqObj.get(IMAGE_WIDTH_FIELD));
            }

            if (reqObj.has(IMAGE_DPI_FIELD) && reqObj.get(IMAGE_DPI_FIELD).isJsonPrimitive()) {
                auxData.add(IMAGE_DPI_FIELD, reqObj.get(IMAGE_DPI_FIELD));
            }


            MathJaxConversion conversion = conversionFactory.getConversion(imageFormatToGenerate, auxData);

            imageData = conversion.convert(mathTex);

        } catch (RequestNotAJsonObjectException requestNotAJsonObjectException) {
            String errMsg = "Request body is not a JSON object";
            logger.severe(errMsg + ": " + requestNotAJsonObjectException.getMessage());

            errors.add(new JsonPrimitive(errMsg));

        } catch (MissingMathTexException missingMathTexExc) {
            String errMsg = "Missing mathTex field in request";
            logger.severe(errMsg + ": " + missingMathTexExc.getMessage());

            errors.add(new JsonPrimitive(errMsg));

        } catch (IOException ioExc) {
            String errMsg = "Conversion failed due to IOException";
            logger.severe(errMsg + ": " + ioExc.getMessage());

            errors.add(new JsonPrimitive(errMsg));
        } catch (IllegalArgumentException iaExc) {
            String errMsg = "Conversion failed due to IllegalArgumentException" + ": " + iaExc.getMessage();
            logger.severe(errMsg);

            errors.add(new JsonPrimitive(errMsg));

        }

        /* Prepare the response */
        response.setContentType("application/json");
        JsonObject outObj = new JsonObject();
        outObj.add(RESP_ERRORS_FIELD, errors);

        if (mathTex != null) {
            outObj.add(MATH_TEX_FIELD, new JsonPrimitive(mathTex));
        }

        if (imageData != null) {
            outObj.add(CONVERSION_RESULT_FIELD, new JsonPrimitive(imageData));
        }

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(gson.toJson(outObj));
        } catch (IOException exc) {

        } finally {
            out.close();
        }
    }



}