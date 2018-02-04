package com.jw.flickrfeed.utils;

import android.support.annotation.NonNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class TestResources {

    /**
     * Reads a string resource from the given resource path.
     *
     * @param resourcePath a destination relative to the resources directory
     * @param encoding     an encoding to use for the produced string
     */
    @NonNull
    public static String readFile(@NonNull String resourcePath, @NonNull String encoding) throws IOException {
        final StringBuilder buffer = new StringBuilder();

        try (InputStream stream = TestResources.class.getResourceAsStream(resourcePath)) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, encoding))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line).append('\n');
                }

                return buffer.toString();
            }
        }
    }
}
