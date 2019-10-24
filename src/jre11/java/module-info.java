import com.guicedee.guicedinjection.abstractions.GuiceInjectorModule;
import com.guicedee.guicedinjection.implementations.GuiceDefaultModuleExclusions;
import com.guicedee.guicedinjection.implementations.ObjectMapperBinder;
import com.guicedee.guicedinjection.injections.ContextBinderGuice;
import com.guicedee.guicedinjection.interfaces.*;

module com.guicedee.guicedinjection {

	requires com.google.guice;

	requires io.github.classgraph;
	requires java.validation;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.datatype.jdk8;

	requires java.logging;
	requires transitive com.guicedee.logmaster;

	requires aopalliance;
	requires javax.inject;

	requires com.google.common;
	requires com.fasterxml.jackson.module.guice;

	exports com.guicedee.guicedinjection;
	exports com.guicedee.guicedinjection.interfaces;
	exports com.guicedee.guicedinjection.interfaces.annotations;
	exports com.guicedee.guicedinjection.abstractions;
	exports com.guicedee.guicedinjection.pairing;
	exports com.guicedee.guicedinjection.properties;

	uses IPackageContentsScanner;
	uses IFileContentsScanner;
	uses IGuiceConfigurator;
	uses IGuiceDefaultBinder;
	uses IGuicePreStartup;
	uses IGuicePreDestroy;
	uses IGuiceModule;
	uses IGuicePostStartup;
	uses IPathContentsScanner;
	uses IPathContentsBlacklistScanner;
	uses IGuiceScanJarExclusions;
	uses IGuiceScanModuleExclusions;
	uses IGuiceScanJarInclusions;
	uses IGuiceScanModuleInclusions;
	uses IPackageBlackListScanner;

	provides IGuiceScanJarExclusions with GuiceDefaultModuleExclusions;
	provides IGuiceScanModuleExclusions with GuiceDefaultModuleExclusions;

	provides IGuiceDefaultBinder with ContextBinderGuice, ObjectMapperBinder;
	provides IGuiceModule with GuiceInjectorModule;
	provides IGuicePreDestroy with JobService;

	opens com.guicedee.guicedinjection to com.fasterxml.jackson.databind;
	opens com.guicedee.guicedinjection.properties to com.fasterxml.jackson.databind;
}
