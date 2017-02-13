package com.armineasy.injection.interfaces;

import com.armineasy.injection.abstractions.GuiceSiteInjectorModule;
import java.util.Comparator;

/**
 * Assists to bind into the injection module from multiple JAR or enterprise archives
 *
 * @author GedMarc
 * @since 12 Dec 2016
 *
 */
public abstract class GuiceSiteBinder implements Comparator<GuiceSiteBinder>, DefaultBinder<GuiceSiteInjectorModule>
{

    protected static String QueryParametersRegex = "(\\?.*)?";

    private int DefaultSortOrder = 100;

    /**
     * Blank constructor
     */
    public GuiceSiteBinder()
    {
        //Nothing needed to be done
    }

    /**
     * The default sort order number is 100
     *
     * @return
     */
    public Integer sortOrder()
    {
        return DefaultSortOrder;
    }

    public void setDefaultSortOrder(int DefaultSortOrder)
    {
        this.DefaultSortOrder = DefaultSortOrder;
    }

    /**
     * Compares the items across
     *
     * @param o1
     * @param o2
     *
     * @return
     */
    @Override
    public int compare(GuiceSiteBinder o1, GuiceSiteBinder o2)
    {
        if (o1 == null || o2 == null)
        {
            return -1;
        }
        return o1.sortOrder().compareTo(o2.sortOrder());
    }
}
