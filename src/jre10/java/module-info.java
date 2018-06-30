module com.jwebmp.guicedinjection {

	requires com.google.guice;

	requires io.github.lukehutch.fastclasspathscanner;
	requires javax.xml;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotations;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

	exports com.jwebmp.guicedinjection.scanners;
	exports com.jwebmp.guicedinjection;
	exports com.jwebmp.guicedinjection.interfaces;
	exports com.jwebmp.guicedinjection.abstractions;

	uses com.jwebmp.guicedinjection.scanners.PackageContentsScanner;
	uses com.jwebmp.guicedinjection.scanners.FileContentsScanner;
	uses com.jwebmp.guicedinjection.interfaces.GuiceConfigurator;

	provides com.jwebmp.guicedinjection.scanners.PackageContentsScanner with com.jwebmp.guicedinjection.scanners.GuiceInjectionPackageDefaultInclusions;
}
1
