package io.github.engagelab.bean.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VoiceResult {
    @JsonProperty("language")
    private String language;

    @JsonProperty("file_url")
    private String fileUrl;
}
