import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.implementations.GuiceDefaultJARExclusions;
import com.jwebmp.guicedinjection.implementations.GuiceDefaultModuleExclusions;
import com.jwebmp.guicedinjection.injections.ContextBinderGuice;
import com.jwebmp.guicedinjection.injections.JPMSGuiceASM;
import com.jwebmp.guicedinjection.interfaces.*;

module com.jwebmp.guicedinjection {

	requires transitive com.google.guice;
	requires transitive javax.inject;
	requires transitive aopalliance;

	requires transitive com.google.common;

	requires transitive io.github.classgraph;
	requires transitive java.validation;

	requires transitive com.fasterxml.jackson.core;
	requires transitive com.fasterxml.jackson.databind;
	requires transitive com.fasterxml.jackson.datatype.jdk8;
	requires transitive com.fasterxml.jackson.datatype.jsr310;
	requires transitive com.fasterxml.jackson.annotation;

	requires transitive java.logging;
	requires transitive com.jwebmp.logmaster;


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

	provides IGuicePreStartup with JPMSGuiceASM;

	provides IGuiceScanJarExclusions with GuiceDefaultJARExclusions;
	provides IGuiceScanModuleExclusions with GuiceDefaultModuleExclusions;

	provides IGuiceDefaultBinder with ContextBinderGuice;
	provides IGuiceModule with GuiceInjectorModule;
}

