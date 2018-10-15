package za.co.mmagon.externalpackage;

import com.jwebmp.guicedinjection.interfaces.IGuicePostStartup;

public class PostConstructHiddenTestI
		implements IGuicePostStartup<PostConstructHiddenTestI>
{

	public PostConstructHiddenTestI()
	{
	}

	public PostConstructHiddenTestI(String s)
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
