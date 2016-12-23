package com.armineasy.injection.interfaces;

import java.util.Comparator;

/**
 * Maps URL's to Servlet classes across multiple JAR's and/or enterprise archives
 *
 * @author GedMarc
 * @param <GuiceInjectionModule>
 * @since 12 Dec 2016
 *
 */
public abstract class GuiceDefaultBinder<GuiceInjectionModule>
        implements Comparator<GuiceDefaultBinder>, DefaultBinder<GuiceInjectionModule>
{
    private static final int DefaultSortOrder = 100;
    /**
     * Blank constructor
     */
    public GuiceDefaultBinder()
    {
        //Nothing needed to do on constructions
    }

    /**
     * The default value is 100
     * @return 
     */
    public Integer sortOrder()
    {
        return DefaultSortOrder;
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
