package za.co.mmagon.guiceinjection.scanners;

import java.util.HashSet;
import java.util.Set;

public class GuiceInjectionPackageDefaultExclusions implements PackageContentsScanner
{
	@Override
	public Set<String> searchFor()
	{
		Set<String> strings = new HashSet<>();
		strings.add("-com.sun.grizzly");
		strings.add("-com.jcabi");
		strings.add("-junit");
		strings.add("-org.apache.log4j");
		strings.add("-org.apache.tools");
		strings.add("-org.apiguardian");
		strings.add("-org.aspectj");
		strings.add("-FormPreviewFrame$");
		strings.add("-FormPreviewFrame$MyExitAction");
		strings.add("-FormPreviewFrame$MyPackAction");
		strings.add("-FormPreviewFrame$MySetLafAction");
		strings.add("-org.jacoco");
		strings.add("-com.vladium.emma");

		//glassfish jar defaultsglassfish.jar
		strings.add("-com.fasterxml.jackson");
		strings.add("-com.google.common");
		strings.add("-com.google.inject");
		strings.add("-com.microsoft.sqlserver");
		strings.add("-com.sun.enterprise.glassfish");
		strings.add("-com.sun.enterprise.module");
		strings.add("-com.sun.jdi");
		strings.add("-edu.umd.cs.findbugs");
		strings.add("-io.github.lukehutch.fastclasspathscanner");
		strings.add("-javassist");
		strings.add("-net.sf.qualitycheck");
		strings.add("-net.sf.uadetector");
		strings.add("-org.aopalliance");
		strings.add("-org.apache.catalina");
		strings.add("-org.apache.commons");
		strings.add("-org.apache.derby");
		strings.add("-org.glassfish");

		//glassfish jar defaultsglassfish.jar
		strings.add("-org.ietf");
		strings.add("-org.jboss");
		strings.add("-org.jvnet");
		strings.add("-org.slf4j");
		strings.add("-org.w3c");
		strings.add("-org.xml.sax");

		//JBoss Stuffs
		strings.add("-com.sun");
		strings.add("-junit.framework");
		strings.add("-junit.runner");
		strings.add("-junit.textui");
		strings.add("-org.apache");
		strings.add("-org.hamcrest");
		strings.add("-org.junit");
		strings.add("-junit.extensions");
		strings.add("-com.google.thirdparty");
		strings.add("-asposewobfuscated");
		strings.add("-com.hazelcast");
		strings.add("-org.dom4j");
		strings.add("-net.sf.jasperreports");
		strings.add("-org.mozilla.javascript");
		strings.add("-org.openxmlformats");
		strings.add("-com.aspose");
		strings.add("-com.lowagie");
		strings.add("-antlr");
		strings.add("-com.concerto");
		strings.add("-com.itextpdf");
		strings.add("-org.eclipse");
		strings.add("-org.exolab");
		strings.add("-org.hibernate");
		strings.add("-org.tartarus");
		strings.add("-org.olap4j");
		strings.add("-org.joda.time");
		strings.add("-org.xmlpull");
		strings.add("-schemasMicrosoftComOfficeExcel");
		strings.add("-schemasMicrosoftComVml");

		strings.add("-bitronix");
		strings.add("-com.fasterxml");
		strings.add("-com.google.gson");
		strings.add("-com.google.j2objc");
		strings.add("-com.google.javascript");
		strings.add("-com.google.protobuf");
		strings.add("-com.ibm.as400");
		strings.add("-com.ibm");
		strings.add("-com.jcraft");
		strings.add("-com.mchange");
		strings.add("-com.typesafe");
		strings.add("-com.unboundid");
		strings.add("-mediautil");
		strings.add("-microsoft.exchange");
		strings.add("-org.antlr");
		strings.add("-org.atmosphere");
		strings.add("-org.bouncycastle");
		strings.add("-org.datacontract");
		strings.add("-org.drools");
		strings.add("-org.hornetq");
		strings.add("-org.jasypt");
		strings.add("-org.jaxen");
		strings.add("-org.jbpm");
		strings.add("-org.jdom");
		strings.add("-org.jsoup");
		strings.add("-org.mortbay");
		strings.add("-org.mozilla");
		strings.add("-org.mvel2");
		strings.add("-org.omnifaces");
		strings.add("-org.primefaces");
		strings.add("-org.snmp4j");
		strings.add("-org.supercsv");
		strings.add("-repackage");
		strings.add("-schemaorg_apache_xmlbeans");
		strings.add("-schemasMicrosoftComOfficeOffice");
		strings.add("-utilities");
		strings.add("-weblogic");

		strings.add("-org.mockito");
		strings.add("-org.jsr107");
		strings.add("-org.h2");
		strings.add("-org.codehaus");
		strings.add("-org.assertj");
		strings.add("-lombok");

		strings.add("-com.intellij");
		strings.add("-org.intellij");
		strings.add("-org.jetbrains");
		strings.add("-com.microsoft");
		strings.add("-com.nimbusds");
		strings.add("-groovy");
		strings.add("-groovyjarjarantlr");
		strings.add("-groovyjarjarasm");
		strings.add("-org.objenesis");
		strings.add("-net.minidev");
		strings.add("-microsoft.sql");
		strings.add("-org.opentest4j");
		strings.add("-com.oracle.jaxb.jaxb");
		strings.add("-eft");
		strings.add("-Driver");
		strings.add("-FormPreviewFrame");

		strings.add("-com.beust.jcommander");
		strings.add("-__redirected");
		strings.add("-com.github.jaiimageio");
		strings.add("-com.google.zxing");
		strings.add("-net.sf.ehcache");
		strings.add("-generated");

		strings.add("-*.jpg");
		strings.add("-*.jpeg");
		strings.add("-*.gif");
		strings.add("-*.png");
		strings.add("-*.xhtml");
		strings.add("-*.jsf");
		strings.add("-*.jsp");
		strings.add("-*.svg");
		strings.add("-*.txt");
		strings.add("-*.js");
		strings.add("-*.css");
		strings.add("-*.scss");
		strings.add("-*.pdf");
		strings.add("-*.xsd");

		return strings;
	}
}
