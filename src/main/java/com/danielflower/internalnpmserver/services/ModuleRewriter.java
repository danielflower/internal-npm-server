package com.danielflower.internalnpmserver.services;

import java.io.File;

public interface ModuleRewriter {
    void rewriteModule(File original) throws ModuleRewriterException;
}
