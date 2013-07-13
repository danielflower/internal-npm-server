package integration.com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.services.HttpProxyServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ProxyOutputStream;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Response;

import java.io.*;
import java.net.*;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class HttpProxyServiceImplTest {

    private final Mockery context = new JUnit4Mockery();
    private final Response response = context.mock(Response.class);
    private final HttpProxyServiceImpl proxyService = new HttpProxyServiceImpl();
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();


    @Before
    public void init() throws IOException {
        context.checking(new Expectations() {{
            oneOf(response).getOutputStream();
            will(returnValue(out));
        }});
    }

    @Test
    public void theContentIsCopiedFromTheRemoteServerToTheLocalResponseStream() throws Exception {

        URL url = new URL("http://registry.npmjs.org/grunt-contrib-jshint/0.6.0");

        context.checking(new Expectations() {{
            oneOf(response).setValue("Content-Type", "application/json");
        }});
        proxyService.proxy(url, response);
        assertThat(responseText(), equalTo(expectedText()));
    }

    private String responseText() throws IOException {
        return out.toString("UTF-8");
    }

    private String expectedText() {
        return "{\"name\":\"grunt-contrib-jshint\",\"description\":\"Validate files with JSHint.\",\"version\":\"0.6.0\",\"homepage\":\"https://github.com/gruntjs/grunt-contrib-jshint\",\"author\":{\"name\":\"Grunt Team\",\"url\":\"http://gruntjs.com/\"},\"repository\":{\"type\":\"git\",\"url\":\"git://github.com/gruntjs/grunt-contrib-jshint.git\"},\"bugs\":{\"url\":\"https://github.com/gruntjs/grunt-contrib-jshint/issues\"},\"licenses\":[{\"type\":\"MIT\",\"url\":\"https://github.com/gruntjs/grunt-contrib-jshint/blob/master/LICENSE-MIT\"}],\"main\":\"Gruntfile.js\",\"engines\":{\"node\":\">= 0.8.0\"},\"scripts\":{\"test\":\"grunt test\"},\"dependencies\":{\"jshint\":\"~2.1.3\"},\"devDependencies\":{\"grunt-contrib-nodeunit\":\"~0.1.2\",\"grunt-contrib-internal\":\"~0.4.2\",\"grunt\":\"~0.4.0\"},\"peerDependencies\":{\"grunt\":\"~0.4.0\"},\"keywords\":[\"gruntplugin\"],\"contributors\":[{\"name\":\"\\\"Cowboy\\\" Ben Alman\",\"url\":\"http://benalman.com/\"},{\"name\":\"Tyler Kellen\",\"url\":\"http://goingslowly.com/\"}],\"readme\":\"# grunt-contrib-jshint [![Build Status](https://travis-ci.org/gruntjs/grunt-contrib-jshint.png?branch=master)](https://travis-ci.org/gruntjs/grunt-contrib-jshint)\\n\\n> Validate files with JSHint.\\n\\n\\n\\n## Getting Started\\nThis plugin requires Grunt `~0.4.0`\\n\\nIf you haven't used [Grunt](http://gruntjs.com/) before, be sure to check out the [Getting Started](http://gruntjs.com/getting-started) guide, as it explains how to create a [Gruntfile](http://gruntjs.com/sample-gruntfile) as well as install and use Grunt plugins. Once you're familiar with that process, you may install this plugin with this command:\\n\\n```shell\\nnpm install grunt-contrib-jshint --save-dev\\n```\\n\\nOnce the plugin has been installed, it may be enabled inside your Gruntfile with this line of JavaScript:\\n\\n```js\\ngrunt.loadNpmTasks('grunt-contrib-jshint');\\n```\\n\\n\\n\\n\\n## Jshint task\\n_Run this task with the `grunt jshint` command._\\n\\nTask targets, files and options may be specified according to the grunt [Configuring tasks](http://gruntjs.com/configuring-tasks) guide.\\n\\nFor more explanations of the lint errors JSHint will throw at you please visit [jslinterrors.com](http://jslinterrors.com/).\\n\\n### Options\\n\\nAny specified option will be passed through directly to [JSHint][], thus you can specify any option that JSHint supports. See the [JSHint documentation][] for a list of supported options.\\n\\n[JSHint]: http://www.jshint.com/\\n[JSHint documentation]: http://www.jshint.com/docs/\\n\\nA few additional options are supported:\\n\\n#### globals\\nType: `Object`\\nDefault value: `null`\\n\\nA map of global variables, with keys as names and a boolean value to determine if they are assignable. This is not a standard JSHint option, but is passed into the `JSHINT` function as its third argument. See the [JSHint documentation][] for more information.\\n\\n#### jshintrc\\nType: `String`\\nDefault value: `null`\\n\\nIf this filename is specified, options and globals defined therein will be used. The `jshintrc` file must be valid JSON and looks something like this:\\n\\n```json\\n{\\n  \\\"curly\\\": true,\\n  \\\"eqnull\\\": true,\\n  \\\"eqeqeq\\\": true,\\n  \\\"undef\\\": true,\\n  \\\"globals\\\": {\\n    \\\"jQuery\\\": true\\n  }\\n}\\n```\\n\\n*Be aware that `jshintrc` settings are not merged with your Grunt options.*\\n\\n#### extensions\\nType: `String`\\nDefault value: `''`\\n\\nA list of non-dot-js extensions to check.\\n\\n#### ignores\\nType: `Array`\\nDefault value: `null`\\n\\nA list of files and dirs to ignore. This will override your `.jshintignore` file if set and does not merge.\\n\\n#### force\\nType: `Boolean`\\nDefault value: `false`\\n\\nSet `force` to `true` to report JSHint errors but not fail the task.\\n\\n#### reporter\\nType: `String`\\nDefault value: `null`\\n\\nAllows you to modify this plugins output. By default it will use a built-in Grunt reporter. Set the path to your own custom reporter or to one of the built-in JSHint reporters: `jslint` or `checkstyle`.\\n\\nSee also: [Writing your own JSHint reporter.](http://jshint.com/docs/reporter/)\\n\\n#### reporterOutput\\nType: `String`\\nDefault value: `null`\\n\\nSpecify a filepath to output the results of a reporter. If `reporterOutput` is specified then all output will be written to the given filepath instead of printed to stdout.\\n\\n### Usage examples\\n\\n#### Wildcards\\nIn this example, running `grunt jshint:all` (or `grunt jshint` because `jshint` is a [multi task](http://gruntjs.com/configuring-tasks#task-configuration-and-targets)) will lint the project's Gruntfile as well as all JavaScript files in the `lib` and `test` directories and their subdirectores, using the default JSHint options.\\n\\n```js\\n// Project configuration.\\ngrunt.initConfig({\\n  jshint: {\\n    all: ['Gruntfile.js', 'lib/**/*.js', 'test/**/*.js']\\n  }\\n});\\n```\\n\\n#### Linting before and after concatenating\\nIn this example, running `grunt jshint` will lint both the \\\"beforeconcat\\\" set and \\\"afterconcat\\\" sets of files. This is not ideal, because `dist/output.js` may get linted before it gets created via the [grunt-contrib-concat plugin](https://github.com/gruntjs/grunt-contrib-concat) `concat` task.\\n\\nIn this case, you should lint the \\\"beforeconcat\\\" files first, then concat, then lint the \\\"afterconcat\\\" files, by running `grunt jshint:beforeconcat concat jshint:afterconcat`.\\n\\n```js\\n// Project configuration.\\ngrunt.initConfig({\\n  concat: {\\n    dist: {\\n      src: ['src/foo.js', 'src/bar.js'],\\n      dest: 'dist/output.js'\\n    }\\n  },\\n  jshint: {\\n    beforeconcat: ['src/foo.js', 'src/bar.js'],\\n    afterconcat: ['dist/output.js']\\n  }\\n});\\n```\\n\\n#### Specifying JSHint options and globals\\n\\nIn this example, custom JSHint options are specified. Note that when `grunt jshint:uses_defaults` is run, those files are linted using the default options, but when `grunt jshint:with_overrides` is run, those files are linted using _merged_ task/target options.\\n\\n```js\\n// Project configuration.\\ngrunt.initConfig({\\n  jshint: {\\n    options: {\\n      curly: true,\\n      eqeqeq: true,\\n      eqnull: true,\\n      browser: true,\\n      globals: {\\n        jQuery: true\\n      },\\n    },\\n    uses_defaults: ['dir1/**/*.js', 'dir2/**/*.js'],\\n    with_overrides: {\\n      options: {\\n        curly: false,\\n        undef: true,\\n      },\\n      files: {\\n        src: ['dir3/**/*.js', 'dir4/**/*.js']\\n      },\\n    }\\n  },\\n});\\n```\\n\\n#### Ignoring specific warnings\\n\\nIf you would like to ignore a specific warning:\\n\\n```shell\\n[L24:C9] W015: Expected '}' to have an indentation at 11 instead at 9.\\n```\\n\\nYou can toggle it by prepending `-` to the warning id as an option:\\n\\n```js\\ngrunt.initConfig({\\n  jshint: {\\n    ignore_warning: {\\n      options: {\\n        '-W015': true,\\n      },\\n      src: ['**/*.js'],\\n    },\\n  },\\n});\\n```\\n\\n\\n## Release History\\n\\n * 2013-06-02   v0.6.0   Dont always succeed the task when using a custom reporter. Bump jshint to 2.1.3.\\n * 2013-05-22   v0.5.4   Fix default reporter to show offending file.\\n * 2013-05-19   v0.5.3   [object Object]\\n * 2013-05-18   v0.5.2   Fix printing too many erroneous ignored file errors.\\n * 2013-05-17   v0.5.1   Fix for when only 1 file is lint free.\\n * 2013-05-17   v0.5.0   Bump to jshint 2.0. Add support for .jshintignore files and ignores option Add support for extensions option. Add support for custom reporters and output report to a file.\\n * 2013-04-08   v0.4.3   Fix evaluation of predef option when it's an object.\\n * 2013-04-08   v0.4.2   Avoid wiping force option when jshintrc is used.\\n * 2013-04-06   v0.4.1   Fix to allow object type for deprecated predef.\\n * 2013-04-04   v0.4.0   Revert task level options to override jshintrc files.\\n * 2013-03-13   v0.3.0   Bump to JSHint 1.1.0. Add force option to report JSHint errors but not fail the task. Add error/warning code to message. Allow task level options to override jshintrc file.\\n * 2013-02-26   v0.2.0   Bump to JSHint 1.0\\n * 2013-02-15   v0.1.1   First official release for Grunt 0.4.0.\\n * 2013-01-18   v0.1.1rc6   Updating grunt/gruntplugin dependencies to rc6. Changing in-development grunt/gruntplugin dependency versions from tilde version ranges to specific versions.\\n * 2013-01-09   v0.1.1rc5   Updating to work with grunt v0.4.0rc5. Switching to this.filesSrc api.\\n * 2012-10-18   v0.1.0   Work in progress, not yet officially released.\\n\\n---\\n\\nTask submitted by [\\\"Cowboy\\\" Ben Alman](http://benalman.com/)\\n\\n*This file was generated on Sun Jun 02 2013 20:02:26.*\\n\",\"readmeFilename\":\"README.md\",\"_id\":\"grunt-contrib-jshint@0.6.0\",\"dist\":{\"shasum\":\"e58891a532144f2fdb7edb8e5b37ebc625f34729\",\"tarball\":\"http://registry.npmjs.org/grunt-contrib-jshint/-/grunt-contrib-jshint-0.6.0.tgz\"},\"_from\":\".\",\"_npmVersion\":\"1.2.21\",\"_npmUser\":{\"name\":\"shama\",\"email\":\"kyle@dontkry.com\"},\"maintainers\":[{\"name\":\"cowboy\",\"email\":\"cowboy@rj3.net\"},{\"name\":\"tkellen\",\"email\":\"tyler@sleekcode.net\"},{\"name\":\"shama\",\"email\":\"kyle@dontkry.com\"}],\"directories\":{}}";
    }


}