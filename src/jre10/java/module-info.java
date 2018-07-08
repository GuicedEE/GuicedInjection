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

	exports com.jwebmp.guicedinjection.scanners;
	exports com.jwebmp.guicedinjection;
	exports com.jwebmp.guicedinjection.interfaces;
	exports com.jwebmp.guicedinjection.abstractions;
	exports com.jwebmp.guicedinjection.annotations;

	uses com.jwebmp.guicedinjection.scanners.PackageContentsScanner;
	uses com.jwebmp.guicedinjection.scanners.FileContentsScanner;
	uses com.jwebmp.guicedinjection.interfaces.GuiceConfigurator;

	provides com.jwebmp.guicedinjection.scanners.PackageContentsScanner with com.jwebmp.guicedinjection.scanners.GuiceInjectionPackageDefaultInclusions;
	provides com.jwebmp.guicedinjection.interfaces.GuiceConfigurator with com.jwebmp.guicedinjection.jre10config.GuiceConfigJRE10;
}

