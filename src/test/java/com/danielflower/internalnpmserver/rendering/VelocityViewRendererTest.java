package com.danielflower.internalnpmserver.rendering;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VelocityViewRendererTest {

    private final VelocityViewRenderer renderer = new VelocityViewRenderer("/velocity/");
    private final StringWriter responseContent = new StringWriter();

    @Test
    public void canOpenATemplateFileAndApplyModelToIt() throws IOException {
        renderer.render("with_model", sampleModel(), responseContent);
        assertThat(responseContent.toString(), is(equalToIgnoringWhiteSpace(templateWithModelValues("The Page Title", "Welcome to this page; it's rather nice."))));
    }

    @Test
    public void aTemplateWithNoModelCanPassNullForTheModel() throws IOException {
        renderer.render("without_model", null, responseContent);
        assertThat(responseContent.toString(), is(equalToIgnoringWhiteSpace(templateWithModelValues("No model", "This is a template where no model will be applied."))));
    }

	@Test(expected = RuntimeException.class)
	public void anExceptionIsThrownIfAnExpressionInTheTemplateCannotBeResolved() throws IOException {
		renderer.render("with_model", null, responseContent);
	}

    private String templateWithModelValues(String title, String content) {
        return "<html>\n" +
                "\t<head>\n" +
                "\t\t<title>" + title + "</title>\n" +
                "\t</head>\n" +
                "\t<body>\n" +
                "\t\t<h1>" + title + "</h1>\n" +
                "\t\t<p>" + content + "</p>\n" +
                "\t</body>\n" +
                "</html>";
    }

    private Map<String, Object> sampleModel() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("pageTitle", "The Page Title");
        model.put("introduction", "Welcome to this page; it's rather nice.");
        return model;
    }

    @Test
    public void closesWriterAfterWriting() throws IOException {

        final AtomicBoolean isClosed = new AtomicBoolean(false);

        renderer.render("with_model", sampleModel(), new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
                isClosed.set(true);
            }
        });
        assertThat(isClosed.get(), is(true));
    }

}
