package com.danielflower.internalnpmserver.controllers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class StaticHandlerImplTest {

    private final StaticHandlerImpl staticHandler = new StaticHandlerImpl(new File("src/main/resources/webroot"));
	private final Mockery context = new JUnit4Mockery();
	private final Request request = context.mock(Request.class);
	private final Response response = context.mock(Response.class);
	private final Path path = context.mock(Path.class);
	private final ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
	private final WriterOutputStream outputStream = new WriterOutputStream(new OutputStreamWriter(responseBytes));
	private final WriterOutputStream outputStream2 = new WriterOutputStream(new OutputStreamWriter(new ByteArrayOutputStream()));

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
    public void queryStringsAreIgnoredWhenDeterminingExistence() throws Exception {
        assertThat(staticHandler.canHandle("/favicon.ico?true=dat"), is(true));
    }

    @Test
    public void referencesToParentDirectoriesAreNotAllowed() throws Exception {
        assertThat(staticHandler.canHandle("../views/home.vm"), is(false));
        assertThat(staticHandler.canHandle("~/.ssh/id_rsa"), is(false));
    }

	@Test
	public void writesTheRequestedFileToTheResponseStreamWithRelevantHeadersAndOneYearLongCacheTime() throws Exception {
		File sampleFile = new File("src/main/resources/webroot/foundation.html");
		assertThat("An expected file does not exist so this test is invalid: " + sampleFile.getCanonicalPath(),
				sampleFile.exists(), is(true));

		context.checking(new Expectations() {{
			allowing(request).getPath(); will(returnValue(path));
			allowing(path).getPath(); will(returnValue("/foundation.html"));
			allowing(response).getOutputStream(); will(returnValue(outputStream));

			oneOf(response).setValue("Content-Type", "text/html");
			oneOf(response).setDate(with("Date"), with(any(long.class)));
			oneOf(response).setDate(with("Last-Modified"), with(any(long.class)));
			oneOf(response).setValue("Cache-Control", "max-age=29030400, public");
		}});
		staticHandler.handle(request, response);
		assertThat(responseBytes.toByteArray(), is(equalTo(FileUtils.readFileToByteArray(sampleFile))));
	}

	@Test
	public void returnsTheFileSystemsLastModifiedTimeForLocalFiles() {
		File sampleFile = new File("src/main/resources/webroot/foundation.html");
		Date now = new Date();
		sampleFile.setLastModified(now.getTime());
		assertThat(staticHandler.dateCreated("/foundation.html"), is(equalTo(now)));
	}

	@Test
	public void robotsTxtAndFavIconHaveCacheTimeOfOneWeek() throws Exception {
		context.checking(new Expectations() {{
			allowing(request).getPath(); will(returnValue(path));
			allowing(path).getPath(); will(onConsecutiveCalls(returnValue("/robots.txt"), returnValue("/favicon.ico")));
			allowing(response).getOutputStream(); will(onConsecutiveCalls(returnValue(outputStream), returnValue(outputStream2)));

			exactly(2).of(response).setValue(with("Content-Type"), with(any(String.class)));
			exactly(2).of(response).setDate(with("Date"), with(any(long.class)));
			exactly(2).of(response).setDate(with("Last-Modified"), with(any(long.class)));
			exactly(2).of(response).setValue("Cache-Control", "max-age=604800, public");
		}});
		staticHandler.handle(request, response);
		staticHandler.handle(request, response);
	}

}
