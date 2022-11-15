package com.guicedee.guicedinjection.representations;

import java.util.TreeSet;

public class ManagedSet<J> extends TreeSet<J>
{
	@Override
	public boolean add(J j)
	{
		if (contains(j))
		{
			remove(j);
		}
		return super.add(j);
	}
}
