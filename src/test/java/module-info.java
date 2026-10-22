module guice.injection.tests {
	requires transitive com.guicedee.guicedinjection;
	
	requires static lombok;
	
	requires org.junit.jupiter.api;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
	//requires org.slf4j;
	//requires org.apache.logging.log4j.slf4j2.impl;
	
	opens com.guicedee.tests to org.junit.platform.commons, com.google.guice;
	
}
