package io.github.engagelab.bean.schedule;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.engagelab.bean.push.PushParam;

import java.io.IOException;

/**
 * Accepts both the v4 push request shape ({@code to/body}) and the flattened
 * push shape returned by the schedule detail endpoint.
 */
public class SchedulePushPayloadDeserializer extends JsonDeserializer<PushParam> {

    private static final String[] BODY_FIELDS = {
            "platform", "notification", "message", "live_activity", "voip", "options"
    };

    @Override
    public PushParam deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode source = codec.readTree(parser);
        if (source == null || source.isNull()) {
            return null;
        }

        ObjectNode normalized = ((ObjectNode) source).deepCopy();
        if (!normalized.has("to") && source.has("audience")) {
            normalized.set("to", source.get("audience"));
        }

        if (!normalized.has("body")) {
            ObjectNode body = JsonNodeFactory.instance.objectNode();
            for (String field : BODY_FIELDS) {
                if (source.has(field)) {
                    body.set(field, source.get(field));
                }
            }
            if (!body.isEmpty()) {
                normalized.set("body", body);
            }
        }

        return codec.treeToValue(normalized, PushParam.class);
    }
}
