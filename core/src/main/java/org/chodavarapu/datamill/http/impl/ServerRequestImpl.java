package org.chodavarapu.datamill.http.impl;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.chodavarapu.datamill.http.*;
import org.chodavarapu.datamill.values.Value;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ServerRequestImpl extends AbstractRequestImpl implements ServerRequest {
    private Multimap<String, String> queryParameters;
    private QueryStringDecoder queryStringDecoder;
    private Multimap<String, String> trailingHeaders;

    public ServerRequestImpl(String method, Multimap<String, String> headers, String uri, Charset charset, Entity entity) {
        super(method, headers, uri, entity);

        this.queryStringDecoder = new QueryStringDecoder(uri, charset);
    }

    private Multimap<String, String> extractQueryParameters() {
        Multimap<String, String> queryParameters;

        Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();

            for (Map.Entry<String, List<String>> p: params.entrySet()) {
                String key = p.getKey();

                List<String> values = p.getValue();
                for (String value : values) {
                    builder.put(key, value);
                }
            }

            queryParameters = builder.build();
        } else {
            queryParameters = null;
            queryStringDecoder = null;
        }

        return queryParameters;
    }

    @Override
    public Value firstTrailingHeader(String header) {
        return firstValue(trailingHeaders, header);
    }

    @Override
    public Value firstTrailingHeader(RequestHeader header) {
        return firstTrailingHeader(header.getName());
    }

    @Override
    public Multimap<String, String> queryParameters() {
        if (queryParameters == null && queryStringDecoder != null) {
            queryParameters = extractQueryParameters();
        }

        return queryParameters;
    }

    @Override
    public Map<String, Object> options() {
        return Collections.emptyMap();
    }

    @Override
    public ResponseBuilder respond() {
        return new ResponseBuilderImpl();
    }

    public void setTrailingHeaders(Multimap<String, String> trailingHeaders) {
        this.trailingHeaders = trailingHeaders;
    }

    @Override
    public Multimap<String, String> trailingHeaders() {
        return trailingHeaders;
    }
}
