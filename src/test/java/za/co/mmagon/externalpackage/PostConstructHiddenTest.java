package za.co.mmagon.externalpackage;

import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;

class PostConstructHiddenTest implements GuicePostStartup {

	PostConstructHiddenTest(String s) {
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
