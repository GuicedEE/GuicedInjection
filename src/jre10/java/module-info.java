module com.jwebmp.guiceinjection {

	exports com.jwebmp.guiceinjection.scanners;
	exports com.jwebmp.guiceinjection;

	uses com.jwebmp.guiceinjection.scanners.PackageContentsScanner;
	uses com.jwebmp.guiceinjection.scanners.FileContentsScanner;
	uses com.jwebmp.guiceinjection.interfaces.IGuiceConfigurator;
}
