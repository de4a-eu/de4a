package eu.de4a.connector.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CustomValidationErrorHandler implements ErrorHandler{
	private static final Logger logger = LoggerFactory.getLogger (CustomValidationErrorHandler.class);
    
    public void warning(SAXParseException exception) throws SAXException {
        logger.warn("WARNING Occured");
        exception.printStackTrace();
    }
  
    public void error(SAXParseException exception) throws SAXException {
        logger.error("ERROR Occured");
        exception.printStackTrace();
    }
  
    public void fatalError(SAXParseException exception) throws SAXException {
        logger.error("FATAL ERROR Occured");
        exception.printStackTrace();
    }
 
}