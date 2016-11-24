package com.haochen.renju.resources;

import java.net.URL;

/**
 * Created by Haochen on 2016/11/23.
 */
public class Resource {

    private static final ClassLoader LOADER = Resource.class.getClassLoader();

    private Resource() {}

    public static URL get(String name) {
        return LOADER.getResource(name);
    }
}
