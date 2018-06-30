package za.co.mmagon.externalpackage;

import com.jwebmp.guicedinjection.annotations.GuicePostStartup;

public class PostConstructTest
		implements GuicePostStartup
{

	public PostConstructTest()
	{

	}

	@Override
	public void postLoad()
	{
		System.out.println("Check 1");
	}

	@Override
	public Integer sortOrder()
	{
		return 1;
	}
}
