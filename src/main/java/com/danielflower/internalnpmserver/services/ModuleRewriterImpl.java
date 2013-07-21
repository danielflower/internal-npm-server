package com.danielflower.internalnpmserver.services;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class ModuleRewriterImpl implements ModuleRewriter {
    private static final Logger log = LoggerFactory.getLogger(ModuleRewriterImpl.class);

    @Override
    public void rewriteModule(File original) throws ModuleRewriterException {
        try {
            original = original.getCanonicalFile();
            log.info("Unpacking " + original.getAbsolutePath());
            File workDir = extractAndDeleteOriginal(original);

            File root = getRootFolder(workDir);

            File packageJson = new File(root, "package.json");
            GitHubDependencyInterceptingFileDownloader.updateDependenciesInFile(packageJson);

            repackArchive(original, root);

            log.info("Repacked " + original.getAbsolutePath());
            FileUtils.deleteDirectory(workDir);
        } catch (Exception ex) {
            throw new ModuleRewriterException("Error while rewriting " + original.getAbsolutePath(), ex);
        }
    }

    private File getRootFolder(File workDir) {
        File[] files = workDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        return files[0];
    }

    private File extractAndDeleteOriginal(File original) throws IOException {
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        File workDir = new File(original.getParentFile(), "work");
        archiver.extract(original, workDir);
        original.delete();
        return workDir;
    }

    private void repackArchive(File original, File root) throws IOException {
        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        File archive = archiver.create(original.getName(), new File(original.getParentFile(), "output"), root);
        FileUtils.moveFile(archive, original);
    }
}
