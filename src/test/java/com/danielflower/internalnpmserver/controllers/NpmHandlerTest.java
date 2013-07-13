package com.danielflower.internalnpmserver.controllers;


import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NpmHandlerTest {

    private NpmHandler handler = new NpmHandler(null);

    @Test
    public void handlesCallsToAllUrlsStartingWithTextNPM() {
        assertThat(handler.canHandle("/npm"), is(true));
        assertThat(handler.canHandle("/npm/"), is(true));
        assertThat(handler.canHandle("/npm/some-module"), is(true));


        assertThat(handler.canHandle("/"), is(false));
        assertThat(handler.canHandle("/anythingelse"), is(false));
    }

}
