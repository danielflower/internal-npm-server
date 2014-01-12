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
public class ReDownloadCachedJSONFilesPolicyTest {

    private final Mockery context = new JUnit4Mockery();
	private final StaticHandler internalRepositoryHolderHandler = context.mock(StaticHandler.class, "internalRepositoryHolderHandler");
	private final StaticHandler remoteCacheHandler = context.mock(StaticHandler.class, "remoteCacheHandler");
    private final String pathToJSON = "/some-package/1.2.3.json";
    private final String pathToNonJSON = "/some-package/_/some-package.1.2.3.tgz";
    private final ReDownloadCachedJSONFilesPolicy policy = new ReDownloadCachedJSONFilesPolicy(internalRepositoryHolderHandler, remoteCacheHandler);

	@Test
	public void ifItIsInTheInternalRepositoryThenNeverDownload() throws Exception {
		final String arbitraryPath = "/blah/man";
		context.checking(new Expectations() {{
			oneOf(internalRepositoryHolderHandler).canHandle(arbitraryPath); will(returnValue(true));
		}});
		assertThat(policy.shouldDownload(arbitraryPath), is(false));
	}

    @Test
    public void ifTheStaticHandlerCannotHandleItThenAlwaysDownload() throws Exception {
        final String arbitraryPath = "/blah/man";
        context.checking(new Expectations() {{
	        oneOf(internalRepositoryHolderHandler).canHandle(arbitraryPath); will(returnValue(false));
	        oneOf(remoteCacheHandler).canHandle(arbitraryPath); will(returnValue(false));
        }});
        assertThat(policy.shouldDownload(arbitraryPath), is(true));
    }

    @Test
    public void ifItIsNotAJSONFileAndItAlreadyExistsThenDoNotDownloadAsTheyShouldBeImmutable() {
        context.checking(new Expectations() {{
	        oneOf(internalRepositoryHolderHandler).canHandle(pathToNonJSON); will(returnValue(false));
	        oneOf(remoteCacheHandler).canHandle(pathToNonJSON);will(returnValue(true));
        }});
        assertThat(policy.shouldDownload(pathToNonJSON), is(false));
    }

    @Test
    public void ifItIsAJSONFileAndItAlreadyExistsThenDownloadItAnyway() {
        context.checking(new Expectations() {{
	        oneOf(internalRepositoryHolderHandler).canHandle(pathToJSON); will(returnValue(false));
	        oneOf(remoteCacheHandler).canHandle(pathToJSON);will(returnValue(true));
        }});
        assertThat(policy.shouldDownload(pathToJSON), is(true));
    }

}
