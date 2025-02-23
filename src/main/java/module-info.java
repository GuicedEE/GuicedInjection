import com.guicedee.guicedinjection.JobService;
import com.guicedee.guicedinjection.implementations.GuiceContextProvision;
import com.guicedee.guicedinjection.implementations.JobServiceProvision;
import com.guicedee.guicedinjection.interfaces.IGuicePreDestroy;
import com.guicedee.guicedinjection.interfaces.IGuiceProvider;
import com.guicedee.guicedinjection.interfaces.IJobServiceProvider;

module com.guicedee.guicedinjection {
	requires transitive com.guicedee.client;
	
	requires transitive com.google.guice;
	requires transitive io.github.classgraph;
	
	//requires transitive com.guicedee.logmaster;
	
	requires transitive org.apache.commons.lang3;

	requires static lombok;
    requires org.apache.logging.log4j.core;
	requires com.guicedee.vertx;

    exports com.guicedee.guicedinjection;
	//exports com.guicedee.guicedinjection.exceptions;
	//exports com.guicedee.guicedinjection.abstractions;
	//exports com.guicedee.guicedinjection.pairing;
	//exports com.guicedee.services.jsonrepresentation.json;
	exports com.guicedee.guicedinjection.representations;
	
	uses com.guicedee.guicedinjection.interfaces.IPackageContentsScanner;
	uses com.guicedee.guicedinjection.interfaces.IFileContentsScanner;
	uses com.guicedee.guicedinjection.interfaces.IFileContentsPatternScanner;
	uses com.guicedee.guicedinjection.interfaces.IGuiceConfigurator;
	//uses com.guicedee.guicedinjection.interfaces.IGuiceDefaultBinder;
	uses com.guicedee.guicedinjection.interfaces.IGuicePreStartup;
	uses com.guicedee.guicedinjection.interfaces.IGuicePreDestroy;
	
	
	uses com.guicedee.guicedinjection.interfaces.IGuiceModule;
	uses com.guicedee.guicedinjection.interfaces.IGuicePostStartup;
	uses com.guicedee.guicedinjection.interfaces.IPathContentsScanner;
	uses com.guicedee.guicedinjection.interfaces.IPathContentsRejectListScanner;
	uses com.guicedee.guicedinjection.interfaces.IGuiceScanModuleExclusions;
	uses com.guicedee.guicedinjection.interfaces.IGuiceScanModuleInclusions;
	uses com.guicedee.guicedinjection.interfaces.IPackageRejectListScanner;
	uses com.guicedee.guicedinjection.interfaces.IGuiceScanJarExclusions;
	uses com.guicedee.guicedinjection.interfaces.IGuiceScanJarInclusions;
    uses com.guicedee.guicedinjection.interfaces.Log4JConfigurator;

    provides com.guicedee.guicedinjection.interfaces.IGuiceScanModuleExclusions with com.guicedee.guicedinjection.implementations.GuiceDefaultModuleExclusions;
	provides com.guicedee.guicedinjection.interfaces.IGuiceScanJarExclusions with com.guicedee.guicedinjection.implementations.GuiceDefaultModuleExclusions;
	
	provides com.guicedee.guicedinjection.interfaces.IGuiceModule with com.guicedee.guicedinjection.injections.ContextBinderGuice;
	//provides com.guicedee.guicedinjection.interfaces.IGuiceModule with com.guicedee.guicedinjection.abstractions.GuiceInjectorModule;
	provides IGuiceProvider with GuiceContextProvision;
	provides IJobServiceProvider with JobServiceProvision;
	
	provides IGuicePreDestroy with JobService;
	
	provides java.net.spi.URLStreamHandlerProvider with com.guicedee.guicedinjection.urls.JrtUrlHandler;
	
	opens com.guicedee.guicedinjection to com.fasterxml.jackson.databind;
}
