package za.co.mmagon.externalpackage;

import com.jwebmp.guicedinjection.annotations.GuicePostStartup;

public class PostConstructHiddenTest
		implements GuicePostStartup
{

	public PostConstructHiddenTest()
	{
	}

	public PostConstructHiddenTest(String s)
	{

	}

	@Override
	public void postLoad()
	{
		System.out.println("Check 2");
	}

	@Override
	public Integer sortOrder()
	{
		return 1;
	}
}
