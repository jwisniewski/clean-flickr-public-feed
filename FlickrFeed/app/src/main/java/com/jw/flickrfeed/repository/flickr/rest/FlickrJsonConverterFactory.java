package com.jw.flickrfeed.repository.flickr.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Converter;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
interface FlickrJsonConverterFactory {

    static Converter.Factory create() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

        return JacksonConverterFactory.create(mapper);
    }
}
