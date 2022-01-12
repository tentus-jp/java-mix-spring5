package jp.tentus.web.mix.spring5;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AssertTests {

    @Autowired
    private Asset asset;

    @Test
    public void testUrl() {
        String url = this.asset.url("/js/vendor.js");

        Assert.assertEquals(url, "/js/vendor.js?id=6b6be5c177492a09c09f");
    }

}
