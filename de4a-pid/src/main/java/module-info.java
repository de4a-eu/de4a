open  module de4apid {
	exports eu.de4a.scsp.translate.birth;
	exports eu.de4a.scsp.translate;
	exports eu.toop.scsp.spring;
	exports eu.de4a.scsp.manager; 
	exports eu.de4a.scsp.ws.client; 
	requires com.helger.commons; 
	requires de4acommons;
	requires org.apache.commons.logging;
	requires java.xml;
	requires eu.toop.connector.api; 
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
}