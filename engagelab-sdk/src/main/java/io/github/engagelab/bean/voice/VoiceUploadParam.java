package io.github.engagelab.bean.voice;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@Data
@AllArgsConstructor
public class VoiceUploadParam {
    private String language;
    private File file;
}
