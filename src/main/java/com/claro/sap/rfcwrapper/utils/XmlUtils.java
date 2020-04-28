package com.claro.sap.rfcwrapper.utils;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Optional;

/**
 * Clase de utilidad que contiene métodos que ayudan al procesamiento de información en formato XML.
 */
public final class XmlUtils {
    private XmlUtils( ) {};

    /**
     * Convierte un xml en formato String a un documento
     * @param xml datos del xml
     * @return Optional del documento XML
     * @see Optional
     */
    public static Optional<Document> fromString(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xml ) ) );
            return Optional.of(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
