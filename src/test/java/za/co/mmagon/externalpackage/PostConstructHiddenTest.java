package za.co.mmagon.externalpackage;

import com.google.inject.Inject;
import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;

class PostConstructHiddenTest implements GuicePostStartup {

	public PostConstructHiddenTest() {
	}

	public PostConstructHiddenTest(String s) {

	}

	@Override
	public void postLoad() {
		System.out.println("Check 2");
	}

	@Override
	public Integer sortOrder() {
		return 1;
	}
}
