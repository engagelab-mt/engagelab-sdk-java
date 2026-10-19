package io.github.engagelab.nativecheck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Client;
import io.github.engagelab.api.*;
import io.github.engagelab.bean.device.*;
import io.github.engagelab.bean.image.ImageParam;
import io.github.engagelab.bean.plan.PushPlanDeleteParam;
import io.github.engagelab.bean.plan.PushPlanListParam;
import io.github.engagelab.bean.plan.PushPlanMsgQueryParam;
import io.github.engagelab.bean.plan.PushPlanParam;
import io.github.engagelab.bean.push.BatchPushParam;
import io.github.engagelab.bean.push.GroupPushParam;
import io.github.engagelab.bean.push.PushParam;
import io.github.engagelab.bean.schedule.*;
import io.github.engagelab.bean.status.*;
import io.github.engagelab.enums.Platform;
import java.io.File;
import java.util.*;

final class FullActions {
  static final ObjectMapper JSON = new ObjectMapper().registerModule(new JavaTimeModule());
  final String base, key, secret, gkey, gsecret;
  final Client transport;

  FullActions(String base, String key, String secret, String gkey, String gsecret, Client t) {
    this.base = base;
    this.key = key;
    this.secret = secret;
    this.gkey = gkey;
    this.gsecret = gsecret;
    this.transport = t;
  }

  <T> T val(JsonNode n, TypeReference<T> t) {
    return JSON.convertValue(n, t);
  }

  Object call(String op, ArrayNode a) throws Exception {
    switch (op) {
      case "push":
        {
          PushApi api =
              new PushApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          PushParam v0 = val(a.get(0), new TypeReference<PushParam>() {});
          return api.push(v0);
        }
      case "validate":
        {
          PushApi api =
              new PushApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          PushParam v0 = val(a.get(0), new TypeReference<PushParam>() {});
          return api.validate(v0);
        }
      case "push_raw":
        {
          PushApi api =
              new PushApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          Object v0 = val(a.get(0), new TypeReference<Object>() {});
          return api.push(v0);
        }
      case "batch_regid":
        {
          PushApi api =
              new PushApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          BatchPushParam v0 = val(a.get(0), new TypeReference<BatchPushParam>() {});
          return api.batchPushByRegId(v0);
        }
      case "batch_alias":
        {
          PushApi api =
              new PushApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          BatchPushParam v0 = val(a.get(0), new TypeReference<BatchPushParam>() {});
          return api.batchPushByAlias(v0);
        }
      case "withdraw":
        {
          PushApi api =
              new PushApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          return api.withdrawPush(v0);
        }
      case "device_get":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          return api.getDevice(v0);
        }
      case "device_set":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          ObjectNode clean = a.get(1).deepCopy();
          boolean clear = clean.path("tags").isTextual() && clean.path("tags").asText().isEmpty();
          if (clear) clean.remove("tags");
          DeviceSetParam v1 = JSON.convertValue(clean, DeviceSetParam.class);
          if (clear) v1.clearTags();
          api.setDevice(v0, v1);
          return null;
        }
      case "device_delete":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          api.deleteDevice(v0);
          return null;
        }
      case "device_status":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          DeviceStatusGetParam v0 = val(a.get(0), new TypeReference<DeviceStatusGetParam>() {});
          return api.getDeviceStatus(v0);
        }
      case "token":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          DeviceTokenRegisterParam v0 =
              val(a.get(0), new TypeReference<DeviceTokenRegisterParam>() {});
          return api.registerToken(v0);
        }
      case "tags":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          return api.getTags();
        }
      case "tag_set":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          TagSetParam v1 = val(a.get(1), new TypeReference<TagSetParam>() {});
          api.setTag(v0, v1);
          return null;
        }
      case "tag_delete":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          List<Platform> v1 = val(a.get(1), new TypeReference<List<Platform>>() {});
          api.deleteTag(v0, v1);
          return null;
        }
      case "tag_status":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          String v1 = val(a.get(1), new TypeReference<String>() {});
          return api.getTagStatus(v0, v1);
        }
      case "tag_count":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          List<String> v0 = val(a.get(0), new TypeReference<List<String>>() {});
          Platform v1 = val(a.get(1), new TypeReference<Platform>() {});
          return api.getTagCount(v0, v1);
        }
      case "tag_quota":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          List<String> v0 = val(a.get(0), new TypeReference<List<String>>() {});
          Platform v1 = val(a.get(1), new TypeReference<Platform>() {});
          return api.getTagQuota(v0, v1);
        }
      case "alias_get":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          List<Platform> v1 = val(a.get(1), new TypeReference<List<Platform>>() {});
          return api.getAliasStatus(v0, v1);
        }
      case "alias_delete":
        {
          DeviceApi api =
              new DeviceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          List<Platform> v1 = val(a.get(1), new TypeReference<List<Platform>>() {});
          api.deleteAlas(v0, v1);
          return null;
        }
      case "schedule_create":
        {
          ScheduleApi api =
              new ScheduleApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          SchedulePushParam v0 = val(a.get(0), new TypeReference<SchedulePushParam>() {});
          return api.schedulePush(v0);
        }
      case "schedule_raw":
        {
          ScheduleApi api =
              new ScheduleApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          Object v0 = val(a.get(0), new TypeReference<Object>() {});
          return api.schedulePush(v0);
        }
      case "schedule_update":
        {
          ScheduleApi api =
              new ScheduleApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          SchedulePushUpdateParam v0 =
              val(a.get(0), new TypeReference<SchedulePushUpdateParam>() {});
          return api.updateSchedulePush(v0);
        }
      case "schedule_get":
        {
          ScheduleApi api =
              new ScheduleApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          return api.getSchedulePush(v0);
        }
      case "schedule_delete":
        {
          ScheduleApi api =
              new ScheduleApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          api.deleteSchedulePush(v0);
          return null;
        }
      case "schedule_list":
        {
          ScheduleApi api =
              new ScheduleApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          SchedulePushListParam v0 = val(a.get(0), new TypeReference<SchedulePushListParam>() {});
          return api.listSchedulePush(v0);
        }
      case "schedule_msgs":
        {
          ScheduleApi api =
              new ScheduleApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          SchedulePushDetailGetParam v0 =
              val(a.get(0), new TypeReference<SchedulePushDetailGetParam>() {});
          return api.getSchedulePushDetail(v0);
        }
      case "status":
        {
          StatusApi api =
              new StatusApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          MessageStatusGetParam v0 = val(a.get(0), new TypeReference<MessageStatusGetParam>() {});
          return api.getMessageStatus(v0);
        }
      case "lifecycle":
        {
          StatusApi api =
              new StatusApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          MessageLifecycleGetParam v0 =
              val(a.get(0), new TypeReference<MessageLifecycleGetParam>() {});
          return api.getMessageLifecycle(v0);
        }
      case "batch_lifecycle":
        {
          StatusApi api =
              new StatusApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          List<String> v0 = val(a.get(0), new TypeReference<List<String>>() {});
          return api.getBatchMessageLifecycle(v0);
        }
      case "users":
        {
          StatusApi api =
              new StatusApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          UserStatusGetParam v0 = val(a.get(0), new TypeReference<UserStatusGetParam>() {});
          return api.getUserStatus(v0);
        }
      case "plan_status":
        {
          StatusApi api =
              new StatusApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          PlanDetailGetParam v0 = val(a.get(0), new TypeReference<PlanDetailGetParam>() {});
          return api.getPlanDetail(v0);
        }
      case "plan_create":
        {
          PushPlanApi api =
              new PushPlanApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          PushPlanParam v0 = val(a.get(0), new TypeReference<PushPlanParam>() {});
          return api.createOrUpdate(v0);
        }
      case "plan_list":
        {
          PushPlanApi api =
              new PushPlanApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          PushPlanListParam v0 = val(a.get(0), new TypeReference<PushPlanListParam>() {});
          return api.queryList(v0);
        }
      case "plan_msgs":
        {
          PushPlanApi api =
              new PushPlanApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          PushPlanMsgQueryParam v0 = val(a.get(0), new TypeReference<PushPlanMsgQueryParam>() {});
          return api.queryMsgByPlan(v0);
        }
      case "plan_delete":
        {
          PushPlanApi api =
              new PushPlanApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          PushPlanDeleteParam v0 = val(a.get(0), new TypeReference<PushPlanDeleteParam>() {});
          return api.delete(v0);
        }
      case "plan_batch_delete":
        {
          PushPlanApi api =
              new PushPlanApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          List<String> v0 = val(a.get(0), new TypeReference<List<String>>() {});
          return api.batchDelete(v0);
        }
      case "voice_create":
        {
          VoiceApi api =
              new VoiceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          File v1 = new File(a.get(1).asText());
          return api.create(v0, v1);
        }
      case "voices":
        {
          VoiceApi api =
              new VoiceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          return api.list();
        }
      case "voice_get":
        {
          VoiceApi api =
              new VoiceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          return api.get(v0);
        }
      case "voice_delete":
        {
          VoiceApi api =
              new VoiceApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          String v0 = val(a.get(0), new TypeReference<String>() {});
          api.delete(v0);
          return null;
        }
      case "image":
        {
          ImageApi api =
              new ImageApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          ImageParam v0 = val(a.get(0), new TypeReference<ImageParam>() {});
          return api.uploadOppoImage(v0);
        }
      case "vip":
        {
          AppApi api =
              new AppApi.Builder()
                  .setHost(base)
                  .setAppKey(key)
                  .setMasterSecret(secret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          return api.getVipStatus();
        }
      case "group":
        {
          GroupPushApi api =
              new GroupPushApi.Builder()
                  .setHost(base)
                  .setAppKey(gkey)
                  .setMasterSecret(gsecret)
                  .setClient(transport)
                  .setLoggerLevel(feign.Logger.Level.NONE)
                  .build();
          GroupPushParam v0 = val(a.get(0), new TypeReference<GroupPushParam>() {});
          return api.push(v0);
        }
      default:
        throw new IllegalArgumentException("unsupported native operation");
    }
  }
}
