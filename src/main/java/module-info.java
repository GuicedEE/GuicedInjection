import com.guicedee.client.services.*;
import com.guicedee.client.services.config.*;
import com.guicedee.client.services.lifecycle.*;
import com.guicedee.guicedinjection.JobService;
import com.guicedee.guicedinjection.implementations.GuiceContextProvision;
import com.guicedee.client.services.IGuiceProvider;

module com.guicedee.guicedinjection {
	requires transitive com.guicedee.client;
	requires transitive org.apache.commons.lang3;

	requires static lombok;
    requires org.apache.logging.log4j.core;
	requires org.apache.logging.log4j.jul;

	requires transitive com.guicedee.vertx;
	requires io.smallrye.config.core;

    exports com.guicedee.guicedinjection;
	//exports com.guicedee.guicedinjection.exceptions;
	//exports com.guicedee.guicedinjection.abstractions;
	//exports com.guicedee.guicedinjection.pairing;
	//exports com.guicedee.services.jsonrepresentation.json;
	exports com.guicedee.guicedinjection.representations;
	
	uses IPackageContentsScanner;
	uses IFileContentsScanner;
	uses IFileContentsPatternScanner;
	uses IGuiceConfigurator;
	uses IGuicePreStartup;
	uses IGuicePreDestroy;
	
	
	uses IGuiceModule;
	uses IGuicePostStartup;
	uses IPathContentsScanner;
	uses IPathContentsRejectListScanner;
	uses IGuiceScanModuleExclusions;
	uses IGuiceScanModuleInclusions;
	uses IPackageRejectListScanner;
	uses IGuiceScanJarExclusions;
	uses IGuiceScanJarInclusions;
    uses Log4JConfigurator;

    provides IGuiceScanModuleExclusions with com.guicedee.guicedinjection.implementations.GuiceDefaultModuleExclusions;
	provides IGuiceScanJarExclusions with com.guicedee.guicedinjection.implementations.GuiceDefaultModuleExclusions;
	
	provides IGuiceModule with com.guicedee.guicedinjection.injections.ContextBinderGuice;
	//provides com.guicedee.client.services.lifecycle.IGuiceModule with com.guicedee.guicedinjection.abstractions.GuiceInjectorModule;
	provides IGuiceProvider with GuiceContextProvision;
	
	provides IGuicePreDestroy with JobService;
	
	provides java.net.spi.URLStreamHandlerProvider with com.guicedee.guicedinjection.urls.JrtUrlHandler;
	
	opens com.guicedee.guicedinjection to com.fasterxml.jackson.databind;
}
