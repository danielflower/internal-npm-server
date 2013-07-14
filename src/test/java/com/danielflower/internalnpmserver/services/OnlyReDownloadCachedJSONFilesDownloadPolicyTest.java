package com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.controllers.StaticHandler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class OnlyReDownloadCachedJSONFilesDownloadPolicyTest {

    private final Mockery context = new JUnit4Mockery();
    private final StaticHandler staticHandler = context.mock(StaticHandler.class);
    private final String pathToJSON = "/some-package/1.2.3.json";
    private final String pathToNonJSON = "/some-package/_/some-package.1.2.3.tgz";
    private final OnlyReDownloadCachedJSONFilesDownloadPolicy policy = new OnlyReDownloadCachedJSONFilesDownloadPolicy(staticHandler);

    @Test
    public void ifTheStaticHandlerCannotHandleItThenAlwaysDownload() throws Exception {
        final String arbitraryPath = "/blah/man";
        context.checking(new Expectations() {{
            oneOf(staticHandler).canHandle(arbitraryPath); will(returnValue(false));
        }});
        assertThat(policy.shouldDownload(arbitraryPath), is(true));
    }

    @Test
    public void ifItIsNotAJSONFileAndItAlreadyExistsThenDoNotDownloadAsTheyShouldBeImmutable() {
        context.checking(new Expectations() {{
            oneOf(staticHandler).canHandle(pathToNonJSON);will(returnValue(true));
        }});
        assertThat(policy.shouldDownload(pathToNonJSON), is(false));
    }

    @Test
    public void ifItIsAJSONFileAndItAlreadyExistsThenDownloadItAnyway() {
        context.checking(new Expectations() {{
            oneOf(staticHandler).canHandle(pathToJSON);will(returnValue(true));
        }});
        assertThat(policy.shouldDownload(pathToJSON), is(true));
    }

}
