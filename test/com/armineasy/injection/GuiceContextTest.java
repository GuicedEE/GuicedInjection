/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.armineasy.injection;

import com.armineasy.injection.abstractions.GuiceInjectorModule;
import com.armineasy.injection.interfaces.GuiceDefaultBinder;
import com.armineasy.injection.interfaces.GuiceSiteBinder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * @author GedMarc
 */
public class GuiceContextTest extends GuiceDefaultBinder
{

	@BeforeAll
	public static void pre()
	{
		LogManager.getLogManager().getLogger("").setLevel(Level.FINEST);
	}

	public static void main(String[] args)
	{
		System.out.println("Main");
		GuiceContext.isBuilt();
		GuiceContext.inject();

		GuiceContext.reflect().getSubTypesOf(GuiceDefaultBinder.class);
		GuiceContext.reflect().getSubTypesOf(GuiceSiteBinder.class);
		GuiceContext.reflect().getTypesAnnotatedWith(com.armineasy.injection.annotations.GuiceInjectorModule.class);
	}

	@Override
	public void onBind(GuiceInjectorModule module)
	{
		System.out.println("Binding Test");
	}

	@Test
	public void testReflection()
	{
		GuiceContext.reflect();
	}

	@Test
	public void testInjection()
	{
		GuiceContext.inject();
	}

}
