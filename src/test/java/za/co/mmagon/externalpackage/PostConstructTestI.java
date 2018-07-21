package za.co.mmagon.externalpackage;

import com.jwebmp.guicedinjection.interfaces.IGuicePostStartup;

public class PostConstructTestI
		implements IGuicePostStartup
{

	public PostConstructTestI()
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
