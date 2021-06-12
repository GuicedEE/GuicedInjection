package com.guicedee.guicedinjection.representations;

import java.util.TreeSet;

public class ManagedSet<J extends ICopyable<J>> extends TreeSet<J>
{
	@Override
	public boolean add(J j)
	{
		if (contains(j))
		{
			this.stream()
			    .filter(tt -> tt.equals(j))
			    .findFirst()
			    .ifPresent(item -> item.updateNonNullField(j));
			return true;
		}
		else
		{
			return super.add(j);
		}
	}
}
