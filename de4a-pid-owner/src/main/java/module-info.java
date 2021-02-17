open module de4a.pid.owner {	
exports eu.de4a.scsp.translate.birth;
exports eu.de4a.scsp.translate;
exports eu.de4a.scsp.spring; 
exports eu.de4a.scsp.ws.client;  
requires   transitive de4acommons; 
requires org.apache.commons.logging;
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
requires java.xml;
requires spring.boot;
requires spring.boot.autoconfigure;
requires  transitive eu.toop.connector.api;
requires java.xml.bind; 
requires java.net.http;
requires commons.fileupload;
requires org.apache.commons.io;
requires spring.webmvc;
requires spring.tx;
requires spring.data.jpa;
requires java.sql;
requires spring.orm;
requires com.zaxxer.hikari;
requires java.persistence;
requires spring.data.commons; 
requires  javax.servlet.api ; 
}
 