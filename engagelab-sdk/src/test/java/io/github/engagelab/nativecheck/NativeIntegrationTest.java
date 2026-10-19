package io.github.engagelab.nativecheck;

import static io.github.engagelab.nativecheck.NativeSupport.*;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.engagelab.api.*;
import io.github.engagelab.bean.device.*;
import io.github.engagelab.bean.plan.*;
import io.github.engagelab.bean.push.*;
import io.github.engagelab.bean.push.options.Options;
import java.util.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
class NativeIntegrationTest {
  @Test
  void fieldPassthrough() throws Exception {
    scenario(
        "field_passthrough",
        w -> {
          PushResult result =
              w.push().validate(payload(w.c.need("ENGAGELAB_TEST_REGISTRATION_ID"), w.c.run));
          w.ok(
              result.getMsgId() != null && result.getMsgId().equals(w.last.path("msg_id").asText()),
              "validate_result_matches_wire");
          w.ok(
              w.sent.at("/body/options/auto_truncation").isBoolean()
                  && !w.sent.at("/body/options/auto_truncation").asBoolean(),
              "false_option_preserved");
          w.ok(
              w.sent.at("/body/notification/android/badge_set_num").isInt()
                  && w.sent.at("/body/notification/android/badge_set_num").asInt() == 0,
              "zero_badge_preserved");
          w.ok(
              w.sent.at("/body/notification/android/is_fold").isBoolean()
                  && !w.sent.at("/body/notification/android/is_fold").asBoolean(),
              "false_fold_preserved");
        });
  }

  @Test
  void pagination101() throws Exception {
    scenario(
        "pagination_101",
        w -> {
          w.c.write();
          PushPlanApi api = w.plan();
          String prefix = w.c.run;
          List<String> owned = new ArrayList<>();
          String query =
              "/v4/push_plan/list?page_index=1&page_size=101&search_description=" + prefix;
          try {
            for (int i = 0; i < 101; i++) {
              String id = prefix + "_" + i;
              owned.add(id);
              PushPlanParam p = new PushPlanParam();
              p.setPlanId(id);
              p.setPlanDescription(prefix);
              w.ok(id.equals(api.createOrUpdate(p).getPlanId()), "created_plan_" + i);
            }
            w.until(() -> w.rest("GET", query).path("total").asInt(-1) == 101);
            PushPlanListResult first = api.queryList(new PushPlanListParam(1, 100, null, prefix));
            PushPlanListResult second = api.queryList(new PushPlanListParam(2, 100, null, prefix));
            PushPlanListResult result = api.queryList(new PushPlanListParam(1, 101, null, prefix));
            JsonNode raw = w.last;
            w.ok(
                first.getPushPlanInfo().size() == 100 && second.getPushPlanInfo().size() == 1,
                "100_plus_1_pages");
            w.ok(
                result.getTotal() == 101 && result.getPushPlanInfo().size() == 101, "101_returned");
            Set<String> ids = new HashSet<>();
            for (PushPlanListResult.PushPlanInfo p : result.getPushPlanInfo()) {
              ids.add(p.getPlanId());
              w.ok(
                  prefix.equals(p.getPlanDescription())
                      && p.getCreateTime() != null
                      && p.getCreateTime() > 0,
                  "plan_fields_preserved");
            }
            w.ok(ids.equals(new HashSet<>(owned)), "owned_ids_exact");
            w.ok(JSON.valueToTree(result).equals(raw), "parsed_fields_match_wire");
          } finally {
            boolean deleted = true;
            for (int i = 0; i < owned.size(); i += 10) {
              try {
                api.batchDelete(owned.subList(i, Math.min(i + 10, owned.size())));
              } catch (Exception ex) {
                deleted = false;
              }
            }
            w.ok(deleted, "cleanup_requests_succeeded");
            w.until(() -> w.rest("GET", query).path("total").asInt(-1) == 0);
            w.ok(true, "cleanup_zero_remaining");
          }
        });
  }

  @Test
  void groupPush() throws Exception {
    scenario(
        "group_push",
        w -> {
          w.c.write();
          Set<String> expected =
              new HashSet<>(Arrays.asList(w.c.need("ENGAGELAB_GROUP_APP_KEYS").split(",")));
          w.ok(expected.size() >= 2 && !expected.contains(""), "two_explicit_members");
          String alias = w.c.need("ENGAGELAB_GROUP_TARGET_ALIAS");
          PushParam source = payload("unused", w.c.run);
          GroupPushParam p = new GroupPushParam();
          p.setBody(source.getBody());
          p.setRequestId(source.getRequestId());
          p.setTo(Collections.singletonMap("alias", Collections.singletonList(alias)));
          Options options = new Options();
          options.setTimeToLive(60);
          options.setThirdPartyChannel(Collections.singletonMap("distribution_new", "mtpush"));
          p.getBody().setOptions(options);
          GroupPushApi api =
              new GroupPushApi.Builder()
                  .setHost(w.c.base)
                  .setAppKey(w.c.need("ENGAGELAB_GROUP_KEY"))
                  .setMasterSecret(w.c.need("ENGAGELAB_GROUP_SECRET"))
                  .setClient(w)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          GroupPushResult result = api.push(p);
          JsonNode raw = w.last;
          w.ok(
              result.getGroupMsgId() != null
                  && !result.getGroupMsgId().isEmpty()
                  && result.getGroupMsgId().equals(raw.path("group_msgid").asText()),
              "group_id_matches_wire");
          w.ok(
              result.getSuccesses().keySet().equals(expected) && result.getErrors().isEmpty(),
              "dynamic_members_all_succeeded");
          for (String app : expected)
            w.ok(
                result.getSuccesses().get(app).getMsgId() != null
                    && result
                        .getSuccesses()
                        .get(app)
                        .getMsgId()
                        .equals(raw.path(app).path("msg_id").asText()),
                "per_app_id_matches_wire");
          w.ok(
              w.sent
                  .path("to")
                  .equals(
                      JSON.valueToTree(
                          Collections.singletonMap("alias", Collections.singletonList(alias)))),
              "explicit_alias_only");
        });
  }

  @Test
  void apnsToken() throws Exception {
    scenario(
        "apns_token",
        w -> {
          w.c.write();
          String token = w.c.need("ENGAGELAB_APNS_TOKEN"),
              channel = w.c.need("ENGAGELAB_APNS_PRODUCTION");
          w.c.need("ENGAGELAB_BUNDLE_ID");
          if (!token.matches("[0-9a-fA-F]{64}")
              || !Arrays.asList("true", "false").contains(channel))
            throw new Blocked("Invalid APNs token/channel");
          DeviceApi api = w.device();
          DeviceTokenRegisterParam p = new DeviceTokenRegisterParam();
          p.setPlatform("ios");
          p.setTokens(Collections.singletonList(token));
          p.setApnsProduction(Boolean.valueOf(channel));
          String rid = null;
          boolean created = false;
          JsonNode baseline = null;
          try {
            DeviceTokenRegisterResult result = api.registerToken(p);
            JsonNode raw = w.last.path("results");
            if (raw.size() == 1) {
              rid = raw.get(0).path("registration_id").asText(null);
              created = raw.get(0).path("is_new").asBoolean();
            }
            w.ok(result.getResults().size() == 1 && raw.size() == 1, "one_token_result");
            DeviceTokenRegisterResult.TokenResult item = result.getResults().get(0);
            w.ok(
                rid != null
                    && !rid.isEmpty()
                    && token.equals(item.getToken())
                    && rid.equals(item.getRegistrationId())
                    && (item.getCode() == null || item.getCode() == 0),
                "registered_token_fields");
            w.ok(
                raw.get(0).path("is_new").isBoolean()
                    && item.getIsNew() != null
                    && item.getIsNew() == raw.get(0).path("is_new").asBoolean(),
                "is_new_matches_wire");
            w.ok(
                w.sent.path("apns_production").isBoolean()
                    && w.sent.path("apns_production").asBoolean() == Boolean.parseBoolean(channel),
                "explicit_apns_channel");
            final String id = rid;
            w.until(
                () -> {
                  w.rest("GET", "/v4/devices/" + id);
                  return w.status == 200;
                });
            baseline = w.rest("GET", "/v4/devices/" + id);
            DeviceTokenRegisterResult again = api.registerToken(p);
            w.ok(
                again.getResults().size() == 1
                    && id.equals(again.getResults().get(0).getRegistrationId())
                    && Boolean.FALSE.equals(again.getResults().get(0).getIsNew()),
                "same_mapping_reused");
          } finally {
            if (rid != null && created) {
              w.rest("DELETE", "/v4/devices/" + rid);
              final String id = rid;
              w.until(
                  () -> {
                    JsonNode b = w.rest("GET", "/v4/devices/" + id);
                    return w.status == 400 && b.path("error").path("code").asInt() == 27002;
                  });
              w.ok(true, "new_mapping_deleted");
            } else if (rid != null && baseline != null)
              w.ok(
                  baseline.equals(w.rest("GET", "/v4/devices/" + rid)) && w.status == 200,
                  "existing_mapping_preserved");
          }
        });
  }
}
