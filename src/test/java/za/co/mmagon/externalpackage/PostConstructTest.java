package za.co.mmagon.externalpackage;

import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;

class PostConstructTest implements GuicePostStartup {

	public PostConstructTest() {

	}

	@Override
	public void postLoad() {
		System.out.println("Check 1");
	}

	@Override
	public Integer sortOrder() {
		return 1;
	}
}
