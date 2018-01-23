package za.co.mmagon.externalpackage;

import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;

class PostConstructTest implements GuicePostStartup {

	PostConstructTest() {
		System.out.println("Found");
	}

	@Override
	public void postLoad() {

	}

	@Override
	public Integer sortOrder() {
		return 1;
	}
}
