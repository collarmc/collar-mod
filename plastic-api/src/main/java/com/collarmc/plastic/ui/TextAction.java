package com.collarmc.plastic.ui;

public abstract class TextAction {
    public static final class OpenLinkAction extends TextAction {
        public final String url;

        public OpenLinkAction(String url) {
            this.url = url;
        }
    }
}
