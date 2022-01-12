package jp.tentus.web.mix.spring5;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Laravel Mix 連携の設定を表します。
 */
@Component
@ConfigurationProperties(prefix = "mix")
@Data
@ToString
public class MixConfiguration {

    /**
     * hot ファイルが有効かどうかを表します。
     */
    private boolean hotFileEnabled = true;

    /**
     * Laravel Mix の hot ファイルのパスを表します。
     */
    private String hotPath = "classpath:static/hot";

    /**
     * manifest ファイルを有効にするかどうかを表します。
     */
    private boolean manifestEnabled = false;

    /**
     * Laravel Mix の manifest ファイルのパスを表します。
     */
    private String manifestPath = "classpath:static/mix-manifest.json";

    /**
     * Laravel Mix の Dev Server ポート番号を表します。
     */
    private int port = 8080;

}
