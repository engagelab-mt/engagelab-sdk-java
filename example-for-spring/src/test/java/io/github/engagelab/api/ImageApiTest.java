package io.github.engagelab.api;

import io.github.engagelab.bean.image.ImageParam;
import io.github.engagelab.bean.image.ImageResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ImageApiTest {

    @Autowired
    private ImageApi imageApi;

    @Test
    public void uploadOppoBigPictureTest() {
        // 大图规格：984×369 px，≤1MB，PNG/JPG/JPEG
        // big_picture_url 和 small_picture_url 不能同时传，需分两次调用
        ImageParam param = new ImageParam();
        param.setBigPictureUrl("https://fastly.picsum.photos/id/727/984/369.jpg?hmac=Kh3XpRHIk-6rxkCBs60XsWf84qfEIXfHofAowML_C58");

        ImageResult result = imageApi.uploadOppoImage(param);
        log.info("上传大图结果: bigPictureId={}", result.getBigPictureId());
    }

    @Test
    public void uploadOppoSmallPictureTest() {
        // 小图标规格：144×144 px，≤50KB，PNG/JPG/JPEG
        ImageParam param = new ImageParam();
        param.setSmallPictureUrl("https://fastly.picsum.photos/id/768/144/144.jpg?hmac=zliHepBfp6voELJdnM_sy4Arrmut3xYS1D11oqpfe8Y");

        ImageResult result = imageApi.uploadOppoImage(param);
        log.info("上传小图结果: smallPictureId={}", result.getSmallPictureId());
    }
}
