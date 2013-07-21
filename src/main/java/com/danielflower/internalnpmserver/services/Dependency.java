package com.danielflower.internalnpmserver.services;

import java.net.MalformedURLException;
import java.net.URL;

public class Dependency {
    public String npmModuleName;
    public String gitHubAuthor;
    public String gitHubRepoName;
    public String gitRef;
    public String fullDependencyReference;

    public Dependency() {
    }

    public Dependency(String npmModuleName, String gitHubAuthor, String gitHubRepoName, String gitRef, String fullDependencyReference) {
        this.npmModuleName = npmModuleName;
        this.gitHubAuthor = gitHubAuthor;
        this.gitHubRepoName = gitHubRepoName;
        this.gitRef = gitRef;
        this.fullDependencyReference = fullDependencyReference;
    }

    @Override
    public String toString() {
        return "{" +
                "npmModuleName='" + npmModuleName + '\'' +
                ", gitHubAuthor='" + gitHubAuthor + '\'' +
                ", gitHubRepoName='" + gitHubRepoName + '\'' +
                ", gitRef='" + gitRef + '\'' +
                ", fullDependencyReference='" + fullDependencyReference + '\'' +
                '}';
    }

    public URL getTarballURL() {
        String url = "https://github.com/" + gitHubAuthor + "/" + gitHubRepoName + "/tarball/" + gitRef;
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error making URL " + url + " for dependency " + this, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dependency that = (Dependency) o;

        if (!fullDependencyReference.equals(that.fullDependencyReference)) return false;
        if (!gitHubAuthor.equals(that.gitHubAuthor)) return false;
        if (!gitHubRepoName.equals(that.gitHubRepoName)) return false;
        if (!gitRef.equals(that.gitRef)) return false;
        if (!npmModuleName.equals(that.npmModuleName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = npmModuleName.hashCode();
        result = 31 * result + gitHubAuthor.hashCode();
        result = 31 * result + gitHubRepoName.hashCode();
        result = 31 * result + gitRef.hashCode();
        result = 31 * result + fullDependencyReference.hashCode();
        return result;
    }
}
