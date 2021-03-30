package eu.de4a.connector.rest;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CustomValidationErrorHandler implements ErrorHandler{
    
    public void warning(SAXParseException exception) throws SAXException {
        System.out.println("WARNING Occured");
        exception.printStackTrace();
    }
  
    public void error(SAXParseException exception) throws SAXException {
        System.out.println("ERROR Occured");
        exception.printStackTrace();
    }
  
    public void fatalError(SAXParseException exception) throws SAXException {
        System.out.println("FATAL ERROR Occured");
        exception.printStackTrace();
    }
 
}