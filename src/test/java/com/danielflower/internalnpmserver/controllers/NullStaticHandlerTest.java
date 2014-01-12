package com.danielflower.internalnpmserver.controllers;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NullStaticHandlerTest {
	@Test
	public void doesNotHandleAnything() throws Exception {
		assertThat(new NullStaticHandler().canHandle("/whatever"), is(false));
	}
}
