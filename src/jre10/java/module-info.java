module com.jwebmp.guicedinjection {

	requires io.github.lukehutch.fastclasspathscanner;
	requires javax.xml;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotations;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

	exports com.jwebmp.guiceinjection.scanners;
	exports com.jwebmp.guiceinjection;

	uses com.jwebmp.guiceinjection.scanners.PackageContentsScanner;
	uses com.jwebmp.guiceinjection.scanners.FileContentsScanner;
	uses com.jwebmp.guiceinjection.interfaces.GuiceConfigurator;

	provides com.jwebmp.guiceinjection.scanners.PackageContentsScanner with com.jwebmp.guiceinjection.scanners.GuiceInjectionPackageDefaultInclusions;
}
