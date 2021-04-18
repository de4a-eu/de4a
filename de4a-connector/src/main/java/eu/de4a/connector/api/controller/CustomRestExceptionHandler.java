package eu.de4a.connector.api.controller;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.UnmarshalException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import eu.de4a.conn.api.rest.ApiError;
/**
 * Controller for handling BAD_REQUEST type errors for more concise messages
 *
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
	@Autowired
	private MessageSource messageSource;
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
	  MissingServletRequestParameterException ex, HttpHeaders headers,
	  HttpStatus status, WebRequest request) {
	  String error = ex.getParameterName() + " parameter is missing";
	  ApiError apiError =  new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
	  return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
			HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			headers.setAccept(mediaTypes);
		}
		List<String> args = new ArrayList<>();
		args.add(String.valueOf(ex.getContentType()));
		args.add(mediaTypes.toString());
		String err= messageSource.getMessage("error.400.mimetype", args.toArray(),LocaleContextHolder.getLocale());
		ApiError apiError =  new ApiError(HttpStatus.BAD_REQUEST, err,""+HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
	}
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable( HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		  String error;
		  if(ex.getCause() instanceof UnmarshalException) {
			  String args[]= {ex.getMessage()};
			  error= messageSource.getMessage("error.400.args.unmarshalling", args,LocaleContextHolder.getLocale());
		  }else {
			  error=messageSource.getMessage("error.400.args.required", null,LocaleContextHolder.getLocale());
		  }
		  ApiError apiError =  new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), error);
		  HttpHeaders httpheader=new HttpHeaders();
		  httpheader.setContentType(   org.springframework.http.MediaType.APPLICATION_XML );
		  return new ResponseEntity<>(apiError, httpheader, apiError.getStatus());
	}
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(
			NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		 	String err= messageSource.getMessage("error.404", null,LocaleContextHolder.getLocale());
		 	ApiError apiError =  new ApiError(HttpStatus.NOT_FOUND, err,""+HttpStatus.NOT_FOUND.value());
			HttpHeaders httpheader=new HttpHeaders();
			httpheader.setContentType(   org.springframework.http.MediaType.APPLICATION_XML );
			return new ResponseEntity<>(apiError, httpheader, apiError.getStatus());
	}
}
