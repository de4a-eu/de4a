module de4acommons {
	exports eu.de4a.exception;
	exports eu.de4a.conn.owner.model;
	exports eu.de4a.conn.api.canonical;
	exports eu.de4a.conn.api.rest;
	exports eu.de4a.util; 

	requires java.xml.bind;
	requires org.apache.logging.log4j;
	requires org.apache.santuario.xmlsec;
	requires spring.core;
	requires transitive spring.web;
	requires org.apache.commons.codec;
	requires org.apache.commons.io;
	requires commons.fileupload;
	requires eu.de4a.edm;
	requires transitive org.slf4j;
	requires eu.toop.connector.api;
	requires com.helger.commons;
	requires com.helger.jaxb;
	requires com.helger.xsds.xml;
	requires com.helger.xsds.ccts.cct.schemamodule;
	requires com.helger.xsds.xlink;
	requires transitive com.helger.xml;
	requires java.xml;
	requires java.activation;
}