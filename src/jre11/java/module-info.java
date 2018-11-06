import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.implementations.GuiceDefaultModuleExclusions;
import com.jwebmp.guicedinjection.injections.ContextBinderGuice;
import com.jwebmp.guicedinjection.injections.JPMSGuiceASM;
import com.jwebmp.guicedinjection.interfaces.*;

module com.jwebmp.guicedinjection {

	requires com.google.guice;

	requires io.github.classgraph;
	requires java.validation;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

	requires java.logging;
	requires com.jwebmp.logmaster;

	requires aopalliance;
	requires javax.inject;
	requires com.fasterxml.jackson.annotation;
	requires com.google.common;

	exports com.jwebmp.guicedinjection;
	exports com.jwebmp.guicedinjection.interfaces;
	exports com.jwebmp.guicedinjection.abstractions;
	exports com.jwebmp.guicedinjection.pairing;
	exports com.jwebmp.guicedinjection.properties;

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

	provides IGuicePreStartup with JPMSGuiceASM;

	provides IGuiceScanJarExclusions with GuiceDefaultModuleExclusions;
	provides IGuiceScanModuleExclusions with GuiceDefaultModuleExclusions;

	provides IGuiceDefaultBinder with ContextBinderGuice;
	provides IGuiceModule with GuiceInjectorModule;
}

