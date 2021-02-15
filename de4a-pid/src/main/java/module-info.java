open  module de4apid { 
	exports eu.de4a.scsp.spring;
	exports eu.de4a.scsp.manager;  
	requires com.helger.commons; 
	requires   transitive de4acommons;
	requires org.apache.commons.logging;
	requires   transitive java.xml;
	requires transitive eu.toop.connector.api; 
	requires org.apache.httpcomponents.httpclient;
	requires org.apache.httpcomponents.httpcore;
	requires org.apache.logging.log4j;
	requires org.apache.wss4j.common;
	requires org.slf4j;
	requires spring.beans;
	requires spring.context;
	requires spring.core;
	requires spring.oxm;
	requires spring.ws.core;
	requires spring.ws.security;
	requires spring.xml;
	requires spring.web;
	requires transitive java.xml.bind;
	requires spring.integration.http;
	requires spring.webmvc;
	requires org.apache.commons.io;
	requires java.annotation;
}