package com.danielflower.webstarter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StaticHandlerTest {

    private StaticHandler staticHandler = new StaticHandler();

    @Test
    public void cannotHandlePathsThatDoNotExist() throws Exception {
        assertThat(staticHandler.canHandle("/nonexistant/path"), is(false));
    }

    @Test
    public void cannotHandleDirectories() throws Exception {
        assertThat(staticHandler.canHandle("/"), is(false));
        assertThat(staticHandler.canHandle("/javascripts"), is(false));
    }

    @Test
    public void anyPathRelativeToTheWebRootFolderCanBeHandled() throws Exception {
        assertThat(staticHandler.canHandle("/favicon.ico"), is(true));
    }

    @Test
    public void referencesToParentDirectoriesAreNotAllowed() throws Exception {
        assertThat(staticHandler.canHandle("../views/home.vm"), is(false));
        assertThat(staticHandler.canHandle("~/.ssh/id_rsa"), is(false));
    }

}
