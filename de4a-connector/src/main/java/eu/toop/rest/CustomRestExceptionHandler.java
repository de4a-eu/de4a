package eu.toop.rest;
 
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import eu.de4a.conn.api.rest.ApiError;
/**
 * Controller for handling BAD_REQUEST type errors for more concise messages
 *
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
	  MissingServletRequestParameterException ex, HttpHeaders headers, 
	  HttpStatus status, WebRequest request) {
	  String error = ex.getParameterName() + " parameter is missing y a peique eso no le mola";
	  ApiError apiError =  new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
	  return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	} 
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
			HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			headers.setAccept(mediaTypes);
		}
		String error = mediaTypes+ " media type is required";
		ApiError apiError =  new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}
	protected @ResponseBody ResponseEntity<Object> handleHttpMessageNotReadable( HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		  String error = "Request is not well-formed:"+ex.getLocalizedMessage() ;
		  ApiError apiError =  new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), error);
		  HttpHeaders httpheader=new HttpHeaders();
		  httpheader.setContentType(   org.springframework.http.MediaType.APPLICATION_XML );
		  return new ResponseEntity<Object>(apiError, httpheader, apiError.getStatus());
	}
}