package org.venity.vgit.exceptions;

public class RedirectException extends Exception {
    private final String url;

    public RedirectException(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
