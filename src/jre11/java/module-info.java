module com.guicedee.guicedinjection {
	requires transitive com.google.guice;
	requires transitive io.github.classgraph;

	requires transitive com.fasterxml.jackson.databind;
	requires transitive com.fasterxml.jackson.annotation;
	requires transitive com.fasterxml.jackson.datatype.jdk8;

	requires transitive com.guicedee.logmaster;

	exports com.guicedee.guicedinjection;
	exports com.guicedee.guicedinjection.interfaces;
	exports com.guicedee.guicedinjection.interfaces.annotations;
	exports com.guicedee.guicedinjection.abstractions;
	exports com.guicedee.guicedinjection.pairing;
	exports com.guicedee.guicedinjection.json;
	exports com.guicedee.guicedinjection.properties;

	uses com.guicedee.guicedinjection.interfaces.IPackageContentsScanner;
	uses com.guicedee.guicedinjection.interfaces.IFileContentsScanner;
	uses com.guicedee.guicedinjection.interfaces.IFileContentsPatternScanner;
	uses com.guicedee.guicedinjection.interfaces.IGuiceConfigurator;
	uses com.guicedee.guicedinjection.interfaces.IGuiceDefaultBinder;
	uses com.guicedee.guicedinjection.interfaces.IGuicePreStartup;
	uses com.guicedee.guicedinjection.interfaces.IGuicePreDestroy;
	uses com.guicedee.guicedinjection.interfaces.IGuiceModule;
	uses com.guicedee.guicedinjection.interfaces.IGuicePostStartup;
	uses com.guicedee.guicedinjection.interfaces.IPathContentsScanner;
	uses com.guicedee.guicedinjection.interfaces.IPathContentsRejectListScanner;
	uses com.guicedee.guicedinjection.interfaces.IGuiceScanModuleExclusions;
	uses com.guicedee.guicedinjection.interfaces.IPackageRejectListScanner;
	uses com.guicedee.guicedinjection.interfaces.IGuiceScanJarExclusions;

	provides com.guicedee.guicedinjection.interfaces.IGuiceScanModuleExclusions with com.guicedee.guicedinjection.implementations.GuiceDefaultModuleExclusions;
	provides com.guicedee.guicedinjection.interfaces.IGuiceScanJarExclusions with com.guicedee.guicedinjection.implementations.GuiceDefaultModuleExclusions;

	provides com.guicedee.guicedinjection.interfaces.IGuiceDefaultBinder with com.guicedee.guicedinjection.injections.ContextBinderGuice, com.guicedee.guicedinjection.implementations.ObjectMapperBinder;
	provides com.guicedee.guicedinjection.interfaces.IGuiceModule with com.guicedee.guicedinjection.abstractions.GuiceInjectorModule;
	provides com.guicedee.guicedinjection.interfaces.IGuicePreDestroy with com.guicedee.guicedinjection.interfaces.JobService;

	provides java.net.spi.URLStreamHandlerProvider with com.guicedee.guicedinjection.urls.JrtUrlHandler;

	opens com.guicedee.guicedinjection to com.fasterxml.jackson.databind;
	opens com.guicedee.guicedinjection.properties to com.fasterxml.jackson.databind;
	opens com.guicedee.guicedinjection.json to com.fasterxml.jackson.databind;

	opens com.guicedee.guicedinjection.pairing;

	exports com.guicedee.guicedinjection.xml;
}
