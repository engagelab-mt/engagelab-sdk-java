package io.github.engagelab.codec;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import io.github.engagelab.bean.voice.VoiceUploadParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

public class VoiceMultipartEncoder implements Encoder {
    private final Encoder delegate;

    public VoiceMultipartEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (!(object instanceof VoiceUploadParam)) {
            delegate.encode(object, bodyType, template);
            return;
        }
        VoiceUploadParam param = (VoiceUploadParam) object;
        String boundary = "engagelab-" + UUID.randomUUID();
        String filename = param.getFile().getName().replace("\"", "");
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            write(output, "--" + boundary + "\r\n");
            write(output, "Content-Disposition: form-data; name=\"language\"\r\n\r\n");
            write(output, param.getLanguage() + "\r\n");
            write(output, "--" + boundary + "\r\n");
            write(output, "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n");
            write(output, "Content-Type: application/octet-stream\r\n\r\n");
            output.write(Files.readAllBytes(param.getFile().toPath()));
            write(output, "\r\n--" + boundary + "--\r\n");
            template.removeHeader("Content-Type");
            template.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            template.body(output.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EncodeException("failed to encode voice multipart request", e);
        }
    }

    private static void write(ByteArrayOutputStream output, String value) throws IOException {
        output.write(value.getBytes(StandardCharsets.UTF_8));
    }
}
