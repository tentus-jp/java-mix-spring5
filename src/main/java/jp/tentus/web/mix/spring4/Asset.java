package jp.tentus.web.mix.spring5;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Laravel Mix が管理するアセットへの参照方法を提供します。
 */
@Component
@Slf4j
public class Asset {

    @Autowired
    private MixConfiguration configuration;

    private volatile Boolean hotEnabled = null;

    private volatile Map<String, String> manifestEntries = null;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    private boolean hot() {
        Boolean result = this.hotEnabled;

        if (result == null) {
            synchronized (this) {
                result = this.hotEnabled;

                if (result == null) {
                    if (this.configuration.isHotFileEnabled()) {
                        Resource hotResource = this.resourceLoader.getResource(this.configuration.getHotPath());

                        result = this.hotEnabled = hotResource.exists();
                    } else {
                        result = this.hotEnabled = false;
                    }
                }
            }
        }

        return result;
    }

    private Map<String, String> manifestEntries() {
        Map<String, String> entries = this.manifestEntries;

        if (entries == null) {
            synchronized (this) {
                entries = this.manifestEntries;

                if (entries == null) {
                    try {
                        Resource manifestResource = this.resourceLoader.getResource(this.configuration.getManifestPath());

                        entries = this.manifestEntries = this.objectMapper.readValue(manifestResource.getInputStream(), new TypeReference<ConcurrentHashMap<String, String>>() {

                        });
                    } catch (IOException ex) {
                        entries = this.manifestEntries = new LinkedHashMap<>();
                    }
                }
            }
        }

        return entries;
    }

    /**
     * Dev Server の稼働状況に応じて、参照するアセットの URL を返します。
     * <p>
     * classpath:static/hot の有無によって切り替えを行うため、
     * Laravel Mix の Public Path が src/main/resources/static になっている必要があります。
     *
     * @param path 対象アセットのパス。
     * @return アセットを参照する URL 。
     */
    public String url(String path) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        UriComponentsBuilder builder;

        if (this.hot() && (attributes instanceof ServletRequestAttributes)) {
            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();

            builder = UriComponentsBuilder
                    .fromHttpRequest(new ServletServerHttpRequest(request))
                    .port(this.configuration.getPort());
        } else {
            builder = UriComponentsBuilder.newInstance();
        }

        if (!this.hot() && this.configuration.isManifestEnabled()) {
            Map<String, String> manifestEntries = this.manifestEntries();

            path = manifestEntries.getOrDefault(path, path);
        }

        return builder
                .replacePath(path)
                .build()
                .toString();
    }

}
