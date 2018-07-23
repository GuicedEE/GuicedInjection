import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.injections.ContextBinderGuice;
import com.jwebmp.guicedinjection.interfaces.*;

module com.jwebmp.guicedinjection {

	requires com.google.guice;

	requires io.github.lukehutch.fastclasspathscanner;
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
	uses IGuiceModule;
	uses IGuicePostStartup;

	provides IGuiceDefaultBinder with ContextBinderGuice;
	provides IGuiceModule with GuiceInjectorModule;
}

