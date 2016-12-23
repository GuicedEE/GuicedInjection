package com.armineasy.injection.interfaces;

import com.armineasy.injection.abstractions.GuiceSiteInjectorModule;
import java.util.Comparator;

/**
 * Assists to bind into the injection module from multiple JAR or enterprise archives
 * @author GedMarc
 * @since 12 Dec 2016
 *
 */
public abstract class GuiceSiteBinder implements Comparator<GuiceSiteBinder>, Comparable<GuiceSiteBinder>, DefaultBinder<GuiceSiteInjectorModule>
{
    /**
     * Blank constructor
     */
    public GuiceSiteBinder()
    {
        //Nothing needed to be done
    }

    /**
     * Performs the on bind
     * @param module 
     */
    @Override
    public abstract void onBind(GuiceSiteInjectorModule module);

    public Integer sortOrder()
    {
        return 100;
    }

    @Override
    public int compare(GuiceSiteBinder o1, GuiceSiteBinder o2)
    {
        if (o1 == null || o2 == null)
        {
            return -1;
        }
        return o1.sortOrder().compareTo(o2.sortOrder());
    }

    @Override
    public int compareTo(GuiceSiteBinder o2)
    {
        if (o2 == null)
        {
            return -1;
        }
        return sortOrder().compareTo(o2.sortOrder());
    }

}
