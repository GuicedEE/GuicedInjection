
module com.jwebmp.guicedinjection {

	requires com.google.guice;

	requires io.github.classgraph;
	requires java.validation;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

	requires java.logging;
	requires transitive com.jwebmp.logmaster;

	requires aopalliance;
	requires javax.inject;

	requires com.google.common;
	requires com.fasterxml.jackson.module.guice;

	exports com.jwebmp.guicedinjection;
	exports com.jwebmp.guicedinjection.interfaces;
	exports com.jwebmp.guicedinjection.interfaces.annotations;
	exports com.jwebmp.guicedinjection.abstractions;
	exports com.jwebmp.guicedinjection.pairing;
	exports com.jwebmp.guicedinjection.properties;

	uses com.jwebmp.guicedinjection.interfaces.IPackageContentsScanner;
	uses com.jwebmp.guicedinjection.interfaces.IFileContentsScanner;
	uses com.jwebmp.guicedinjection.interfaces.IGuiceConfigurator;
	uses com.jwebmp.guicedinjection.interfaces.IGuiceDefaultBinder;
	uses com.jwebmp.guicedinjection.interfaces.IGuicePreStartup;
	uses com.jwebmp.guicedinjection.interfaces.IGuicePreDestroy;
	uses com.jwebmp.guicedinjection.interfaces.IGuiceModule;
	uses com.jwebmp.guicedinjection.interfaces.IGuicePostStartup;
	uses com.jwebmp.guicedinjection.interfaces.IPathContentsScanner;
	uses com.jwebmp.guicedinjection.interfaces.IPathContentsBlacklistScanner;
	uses com.jwebmp.guicedinjection.interfaces.IGuiceScanJarExclusions;
	uses com.jwebmp.guicedinjection.interfaces.IGuiceScanModuleExclusions;
	uses com.jwebmp.guicedinjection.interfaces.IGuiceScanJarInclusions;
	uses com.jwebmp.guicedinjection.interfaces.IGuiceScanModuleInclusions;
	uses com.jwebmp.guicedinjection.interfaces.IPackageBlackListScanner;

	provides com.jwebmp.guicedinjection.interfaces.IGuiceScanJarExclusions with com.jwebmp.guicedinjection.implementations.GuiceDefaultModuleExclusions;
	provides com.jwebmp.guicedinjection.interfaces.IGuiceScanModuleExclusions with com.jwebmp.guicedinjection.implementations.GuiceDefaultModuleExclusions;

	provides com.jwebmp.guicedinjection.interfaces.IGuiceDefaultBinder with com.jwebmp.guicedinjection.injections.ContextBinderGuice,com.jwebmp.guicedinjection.implementations.ObjectMapperBinder;
	provides com.jwebmp.guicedinjection.interfaces.IGuiceModule with com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
	provides com.jwebmp.guicedinjection.interfaces.IGuicePreDestroy with com.jwebmp.guicedinjection.interfaces.JobService;
}
