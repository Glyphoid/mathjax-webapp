package me.scai.plato.mathjax.webapp.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.scai.plato.mathjax.webapp.servlets.conversions.MathJaxConversionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class TestMathJaxServlet {
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();

    private MathJaxServlet servlet;

    @Before
    public void setUp() {
        servlet = new MathJaxServlet();

        try {
            servlet.init();
        } catch (ServletException exc) {
            fail(exc.getMessage());
        }

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testMathTex2Png() {
        MockHttpServletRequest req = new MockHttpServletRequest();


        JsonObject reqBodyJson = new JsonObject();
        reqBodyJson.add("mathTex", new JsonPrimitive("\\frac{1}{\\beta_3}"));
        reqBodyJson.add("imageFormat", new JsonPrimitive(MathJaxConversionFactory.getFormatImagePng()));
        reqBodyJson.add(MathJaxConversionFactory.getImageWidthField(), new JsonPrimitive(200));
        reqBodyJson.add(MathJaxConversionFactory.getImageDpiField(), new JsonPrimitive(400));

        req.setContent(gson.toJson(reqBodyJson).getBytes());

        JsonObject respObj = servletHandleRequest(req);

        assertResponseObjectHasNoErrors(respObj);

        assertTrue(respObj.has(MathJaxServlet.CONVERSION_RESULT_FIELD));
        assertTrue(respObj.get(MathJaxServlet.CONVERSION_RESULT_FIELD).isJsonPrimitive());

        String png = respObj.get(MathJaxServlet.CONVERSION_RESULT_FIELD).getAsString();
        assertFalse(png.isEmpty());
    }

    @Test
    public void testMathTex2Png_malformedRequestJson() {
        MockHttpServletRequest req = new MockHttpServletRequest();

        JsonObject reqBodyJson = new JsonObject();
        reqBodyJson.add("mathTex", new JsonPrimitive("\\frac{1}{\\beta_3}"));
        reqBodyJson.add("imageFormat", new JsonPrimitive(MathJaxConversionFactory.getFormatImagePng()));
        reqBodyJson.add(MathJaxConversionFactory.getImageWidthField(), new JsonPrimitive(200));
        reqBodyJson.add(MathJaxConversionFactory.getImageDpiField(), new JsonPrimitive(400));

        String validRequestJson = gson.toJson(reqBodyJson);
        String invalidRequestJson = validRequestJson.substring(0, validRequestJson.length() - 1);
        req.setContent(invalidRequestJson.getBytes());

        JsonObject respObj = servletHandleRequest(req);

        assertEquals(1, respObj.get(MathJaxServlet.RESP_ERRORS_FIELD).getAsJsonArray().size());
    }

    @Test
    public void testMathTex2Png_invalidImageWidth() {
        MockHttpServletRequest req = new MockHttpServletRequest();

        JsonObject reqBodyJson = new JsonObject();
        reqBodyJson.add("mathTex", new JsonPrimitive("\\frac{1}{\\beta_3}"));
        reqBodyJson.add("imageFormat", new JsonPrimitive(MathJaxConversionFactory.getFormatImagePng()));
        reqBodyJson.add(MathJaxConversionFactory.getImageWidthField(), new JsonPrimitive(-200));
        reqBodyJson.add(MathJaxConversionFactory.getImageDpiField(), new JsonPrimitive(400));

        req.setContent(gson.toJson(reqBodyJson).getBytes());

        JsonObject respObj = servletHandleRequest(req);

        assertEquals(1, respObj.get(MathJaxServlet.RESP_ERRORS_FIELD).getAsJsonArray().size());
    }

    @Test
    public void testMathTex2Png_invalidImageDpi() {
        MockHttpServletRequest req = new MockHttpServletRequest();

        JsonObject reqBodyJson = new JsonObject();
        reqBodyJson.add("mathTex", new JsonPrimitive("\\frac{1}{\\beta_3}"));
        reqBodyJson.add("imageFormat", new JsonPrimitive(MathJaxConversionFactory.getFormatImagePng()));
        reqBodyJson.add(MathJaxConversionFactory.getImageWidthField(), new JsonPrimitive(200));
        reqBodyJson.add(MathJaxConversionFactory.getImageDpiField(), new JsonPrimitive(-400));

        req.setContent(gson.toJson(reqBodyJson).getBytes());

        JsonObject respObj = servletHandleRequest(req);

        assertEquals(1, respObj.get(MathJaxServlet.RESP_ERRORS_FIELD).getAsJsonArray().size());
    }


    @Test
    public void testMathTex2MathML() {
        MockHttpServletRequest req = new MockHttpServletRequest();

        JsonObject reqBodyJson = new JsonObject();
        reqBodyJson.add("mathTex", new JsonPrimitive("\\frac{1}{\\beta_3}"));
        reqBodyJson.add("imageFormat", new JsonPrimitive(MathJaxConversionFactory.getFormatStringMathML()));

        req.setContent(gson.toJson(reqBodyJson).getBytes());

        JsonObject respObj = servletHandleRequest(req);

        assertResponseObjectHasNoErrors(respObj);

        assertTrue(respObj.has(MathJaxServlet.CONVERSION_RESULT_FIELD));
        assertTrue(respObj.get(MathJaxServlet.CONVERSION_RESULT_FIELD).isJsonPrimitive());

        String mathML = respObj.get(MathJaxServlet.CONVERSION_RESULT_FIELD).getAsString();
        assertFalse(mathML.isEmpty());
    }

    @Test
    public void testMathTex2MathML_missingMathML() {
        MockHttpServletRequest req = new MockHttpServletRequest();

        JsonObject reqBodyJson = new JsonObject();
        reqBodyJson.add("imageFormat", new JsonPrimitive(MathJaxConversionFactory.getFormatStringMathML()));

        req.setContent(gson.toJson(reqBodyJson).getBytes());

        JsonObject respObj = servletHandleRequest(req);

        assertEquals(1, respObj.get(MathJaxServlet.RESP_ERRORS_FIELD).getAsJsonArray().size());
    }

    private void assertResponseObjectHasNoErrors(JsonObject respObj) {
        assertEquals(0, respObj.get(MathJaxServlet.RESP_ERRORS_FIELD).getAsJsonArray().size());

        assertTrue(respObj.has(MathJaxServlet.MATH_TEX_FIELD));
        assertTrue(respObj.get(MathJaxServlet.MATH_TEX_FIELD).isJsonPrimitive());
    }

    private JsonObject servletHandleRequest(MockHttpServletRequest req) {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        try {
            servlet.doPost(req, resp);
        } catch (ServletException sExc) {
            fail("Failed due to ServletException: " + sExc.getMessage());
        }

        JsonObject respObj = null;
        try {
            respObj = jsonParser.parse(resp.getContentAsString()).getAsJsonObject();
        }
        catch (UnsupportedEncodingException exc) {
            fail(exc.getMessage());
        }

        assertTrue(respObj.has(MathJaxServlet.RESP_ERRORS_FIELD));
        assertTrue(respObj.get(MathJaxServlet.RESP_ERRORS_FIELD).isJsonArray());

        return respObj;
    }

}
