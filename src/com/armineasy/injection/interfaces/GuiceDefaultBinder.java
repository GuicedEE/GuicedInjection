package com.armineasy.injection.interfaces;

import java.util.Comparator;

/**
 *
 * @author GedMarc
 * @since 12 Dec 2016
 *
 */
public abstract class GuiceDefaultBinder<M> implements Comparator<GuiceDefaultBinder>, DefaultBinder<M>
{

    public GuiceDefaultBinder()
    {

    }

    public abstract void onBind(M module);

    public Integer sortOrder()
    {
        return 100;
    }

    @Override
    public int compare(GuiceDefaultBinder o1, GuiceDefaultBinder o2)
    {
        if (o1 == null || o2 == null)
        {
            return -1;
        }
        return o1.sortOrder().compareTo(o2.sortOrder());
    }

}
