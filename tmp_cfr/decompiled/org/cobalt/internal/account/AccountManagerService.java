/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.UserApiService$UserProperties
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.concurrent.ThreadsKt
 *  kotlin.io.CloseableKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_320
 *  net.minecraft.class_7497
 *  net.minecraft.class_7569
 *  net.minecraft.class_7574
 *  net.minecraft.class_7853
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.cobalt.internal.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.Closeable;
import java.io.File;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.concurrent.ThreadsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_7497;
import net.minecraft.class_7569;
import net.minecraft.class_7574;
import net.minecraft.class_7853;
import org.cobalt.api.notification.NotificationManager;
import org.cobalt.mixin.client.MinecraftAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00ae\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0011\b\u00c0\u0002\u0018\u00002\u00020\u0001:\n\u0089\u0001\u008a\u0001\u008b\u0001\u008c\u0001\u008d\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\f\u0010\rJ\r\u0010\u000e\u001a\u00020\b\u00a2\u0006\u0004\b\u000e\u0010\nJ\r\u0010\u000f\u001a\u00020\b\u00a2\u0006\u0004\b\u000f\u0010\nJ\u0015\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0015\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\b\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0015\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\b\u00a2\u0006\u0004\b\u0017\u0010\u0016J!\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0013\u001a\u00020\b2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0005H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0017\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\u0013\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0017\u0010 \u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b \u0010\u001eJ\u001f\u0010#\u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\b2\u0006\u0010\"\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b#\u0010$J\u0017\u0010'\u001a\u00020&2\u0006\u0010%\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b'\u0010(J\u0017\u0010*\u001a\u00020&2\u0006\u0010)\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b*\u0010(J\u0017\u0010,\u001a\u00020\b2\u0006\u0010+\u001a\u00020&H\u0002\u00a2\u0006\u0004\b,\u0010-J#\u00100\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0/2\u0006\u0010.\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b0\u00101J\u0017\u00103\u001a\u00020\u00142\u0006\u00102\u001a\u00020\u0019H\u0002\u00a2\u0006\u0004\b3\u00104J+\u00109\u001a\u0002082\u0006\u00105\u001a\u00020\b2\u0012\u00107\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b06H\u0002\u00a2\u0006\u0004\b9\u0010:J\u001f\u0010<\u001a\u0002082\u0006\u00105\u001a\u00020\b2\u0006\u0010;\u001a\u000208H\u0002\u00a2\u0006\u0004\b<\u0010=J\u001f\u0010?\u001a\u0002082\u0006\u00105\u001a\u00020\b2\u0006\u0010>\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b?\u0010@J\u0017\u0010C\u001a\u0002082\u0006\u0010B\u001a\u00020AH\u0002\u00a2\u0006\u0004\bC\u0010DJ\u000f\u0010E\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\bE\u0010\u0003J\u000f\u0010F\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\bF\u0010\u0003J\u0017\u0010G\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u0005H\u0002\u00a2\u0006\u0004\bG\u0010HJ\u0017\u0010J\u001a\u00020\b2\u0006\u0010I\u001a\u00020\bH\u0002\u00a2\u0006\u0004\bJ\u0010KJ\u0017\u0010L\u001a\u00020\u00142\u0006\u00105\u001a\u00020\bH\u0002\u00a2\u0006\u0004\bL\u0010\u0016J\u001f\u0010O\u001a\u00020\u00142\u0006\u0010M\u001a\u00020\b2\u0006\u0010N\u001a\u00020\bH\u0002\u00a2\u0006\u0004\bO\u0010PJ#\u0010R\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b062\u0006\u0010Q\u001a\u00020\bH\u0002\u00a2\u0006\u0004\bR\u0010SJ\u0017\u0010V\u001a\u00020U2\u0006\u0010T\u001a\u00020\bH\u0002\u00a2\u0006\u0004\bV\u0010WJ\u0017\u0010Y\u001a\u00020\b2\u0006\u0010X\u001a\u00020\bH\u0002\u00a2\u0006\u0004\bY\u0010KJ\u0017\u0010Z\u001a\u00020\b2\u0006\u0010X\u001a\u00020\bH\u0002\u00a2\u0006\u0004\bZ\u0010KJ\u001b\u0010\\\u001a\u00020\b*\u0002082\u0006\u0010[\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b\\\u0010]J\u0013\u0010^\u001a\u00020\b*\u000208H\u0002\u00a2\u0006\u0004\b^\u0010_R\u0014\u0010a\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u0010bR\u001c\u0010e\u001a\n d*\u0004\u0018\u00010c0c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\be\u0010fR\u001c\u0010h\u001a\n d*\u0004\u0018\u00010g0g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bh\u0010iR\u001c\u0010k\u001a\n d*\u0004\u0018\u00010j0j8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bk\u0010lR\u0014\u0010n\u001a\u00020m8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bn\u0010oR\u0016\u0010p\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bp\u0010qR\u0016\u0010r\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\br\u0010sR\u0016\u0010u\u001a\u00020t8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bu\u0010vR\u0014\u0010x\u001a\u00020w8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bx\u0010yR\u0014\u0010z\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bz\u0010sR\u0014\u0010{\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b{\u0010sR\u0014\u0010|\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b|\u0010sR\u0014\u0010~\u001a\u00020}8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b~\u0010\u007fR\u0016\u0010\u0080\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0080\u0001\u0010sR\u0016\u0010\u0081\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0081\u0001\u0010sR\u0016\u0010\u0082\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0082\u0001\u0010sR\u0016\u0010\u0083\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0083\u0001\u0010sR\u0016\u0010\u0084\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0084\u0001\u0010sR\u0016\u0010\u0085\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0085\u0001\u0010sR\u0016\u0010\u0086\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0086\u0001\u0010sR\u0016\u0010\u0087\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0087\u0001\u0010sR\u0016\u0010\u0088\u0001\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u0088\u0001\u0010s\u00a8\u0006\u008e\u0001"}, d2={"Lorg/cobalt/internal/account/AccountManagerService;", "", "<init>", "()V", "", "Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;", "getAccounts", "()Ljava/util/List;", "", "getStatusMessage", "()Ljava/lang/String;", "", "isBusy", "()Z", "getCurrentSessionName", "getCurrentSessionUuid", "account", "isCurrentAccount", "(Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;)Z", "alias", "", "login", "(Ljava/lang/String;)V", "remove", "existing", "Lorg/cobalt/internal/account/AccountManagerService$MinecraftSession;", "authenticate", "(Ljava/lang/String;Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;)Lorg/cobalt/internal/account/AccountManagerService$MinecraftSession;", "Lorg/cobalt/internal/account/AccountManagerService$OAuthTokens;", "startInteractiveMicrosoftLogin", "(Ljava/lang/String;)Lorg/cobalt/internal/account/AccountManagerService$OAuthTokens;", "refreshToken", "refreshMicrosoftTokens", "code", "codeVerifier", "exchangeAuthorizationCode", "(Ljava/lang/String;Ljava/lang/String;)Lorg/cobalt/internal/account/AccountManagerService$OAuthTokens;", "oauthAccessToken", "Lorg/cobalt/internal/account/AccountManagerService$XboxToken;", "requestXboxLiveToken", "(Ljava/lang/String;)Lorg/cobalt/internal/account/AccountManagerService$XboxToken;", "xboxToken", "requestXstsToken", "xstsToken", "requestMinecraftAccessToken", "(Lorg/cobalt/internal/account/AccountManagerService$XboxToken;)Ljava/lang/String;", "minecraftAccessToken", "Lkotlin/Pair;", "requestMinecraftProfile", "(Ljava/lang/String;)Lkotlin/Pair;", "session", "applySession", "(Lorg/cobalt/internal/account/AccountManagerService$MinecraftSession;)V", "url", "", "params", "Lcom/google/gson/JsonObject;", "postForm", "(Ljava/lang/String;Ljava/util/Map;)Lcom/google/gson/JsonObject;", "body", "postJson", "(Ljava/lang/String;Lcom/google/gson/JsonObject;)Lcom/google/gson/JsonObject;", "bearerToken", "getJson", "(Ljava/lang/String;Ljava/lang/String;)Lcom/google/gson/JsonObject;", "Ljava/net/http/HttpRequest;", "request", "sendJson", "(Ljava/net/http/HttpRequest;)Lcom/google/gson/JsonObject;", "load", "saveLocked", "upsertAccount", "(Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;)V", "codeChallenge", "buildMicrosoftAuthUrl", "(Ljava/lang/String;)Ljava/lang/String;", "tryOpenBrowser", "title", "description", "notifyUser", "(Ljava/lang/String;Ljava/lang/String;)V", "rawQuery", "parseQuery", "(Ljava/lang/String;)Ljava/util/Map;", "raw", "Ljava/util/UUID;", "parseUndashedUuid", "(Ljava/lang/String;)Ljava/util/UUID;", "value", "base64UrlSha256", "urlEncode", "key", "requiredString", "(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;", "bestErrorMessage", "(Lcom/google/gson/JsonObject;)Ljava/lang/String;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "logger", "Lorg/slf4j/Logger;", "Lcom/google/gson/Gson;", "gson", "Lcom/google/gson/Gson;", "Ljava/net/http/HttpClient;", "httpClient", "Ljava/net/http/HttpClient;", "Ljava/io/File;", "storeFile", "Ljava/io/File;", "busy", "Z", "statusMessage", "Ljava/lang/String;", "Lorg/cobalt/internal/account/AccountManagerService$AccountStore;", "store", "Lorg/cobalt/internal/account/AccountManagerService$AccountStore;", "Lkotlin/text/Regex;", "UUID_DASH_PATTERN", "Lkotlin/text/Regex;", "MICROSOFT_CLIENT_ID", "MICROSOFT_SCOPES", "USER_AGENT", "", "OAUTH_PORT", "I", "OAUTH_REDIRECT_URI", "OAUTH_AUTHORIZE_URL", "OAUTH_TOKEN_URL", "XBL_URL", "XSTS_URL", "MINECRAFT_AUTH_URL", "MINECRAFT_PROFILE_URL", "SUCCESS_HTML", "FAILURE_HTML", "ManagedAccount", "AccountStore", "OAuthTokens", "XboxToken", "MinecraftSession", "cobalt"})
@SourceDebugExtension(value={"SMAP\nAccountManagerService.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AccountManagerService.kt\norg/cobalt/internal/account/AccountManagerService\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,605:1\n1068#2:606\n363#2,7:608\n1642#2,10:615\n1915#2:625\n1916#2:627\n1652#2:628\n1642#2,10:629\n1915#2:639\n1916#2:641\n1652#2:642\n296#2,2:643\n1#3:607\n1#3:626\n1#3:640\n*S KotlinDebug\n*F\n+ 1 AccountManagerService.kt\norg/cobalt/internal/account/AccountManagerService\n*L\n88#1:606\n505#1:608,7\n545#1:615,10\n545#1:625\n545#1:627\n545#1:628\n578#1:629,10\n578#1:639\n578#1:641\n578#1:642\n125#1:643,2\n545#1:626\n578#1:640\n*E\n"})
public final class AccountManagerService {
    @NotNull
    public static final AccountManagerService INSTANCE = new AccountManagerService();
    @NotNull
    private static final class_310 mc;
    private static final Logger logger;
    private static final Gson gson;
    private static final HttpClient httpClient;
    @NotNull
    private static final File storeFile;
    private static volatile boolean busy;
    @NotNull
    private static volatile String statusMessage;
    @NotNull
    private static AccountStore store;
    @NotNull
    private static final Regex UUID_DASH_PATTERN;
    @NotNull
    private static final String MICROSOFT_CLIENT_ID = "757bb3b3-b7ca-4bcd-a160-c92e6379c263";
    @NotNull
    private static final String MICROSOFT_SCOPES = "XboxLive.signin XboxLive.offline_access";
    @NotNull
    private static final String USER_AGENT = "Mozilla/5.0 (Cobalt Account Manager)";
    private static final int OAUTH_PORT = 3000;
    @NotNull
    private static final String OAUTH_REDIRECT_URI = "http://127.0.0.1:3000";
    @NotNull
    private static final String OAUTH_AUTHORIZE_URL = "https://login.live.com/oauth20_authorize.srf";
    @NotNull
    private static final String OAUTH_TOKEN_URL = "https://login.live.com/oauth20_token.srf";
    @NotNull
    private static final String XBL_URL = "https://user.auth.xboxlive.com/user/authenticate";
    @NotNull
    private static final String XSTS_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    @NotNull
    private static final String MINECRAFT_AUTH_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    @NotNull
    private static final String MINECRAFT_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    @NotNull
    private static final String SUCCESS_HTML = "<!doctype html><html><body style=\"font-family:sans-serif;padding:32px;background:#111;color:#f4f4f4;\"><h2>Microsoft Login Complete</h2><p>You can return to Minecraft now.</p></body></html>";
    @NotNull
    private static final String FAILURE_HTML = "<!doctype html><html><body style=\"font-family:sans-serif;padding:32px;background:#111;color:#f4f4f4;\"><h2>Microsoft Login Failed</h2><p>Return to Minecraft and check the log.</p></body></html>";

    private AccountManagerService() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public final List<ManagedAccount> getAccounts() {
        List list;
        AccountManagerService accountManagerService = this;
        synchronized (accountManagerService) {
            boolean bl = false;
            Iterable $this$sortedBy$iv = store.getAccounts();
            boolean $i$f$sortedBy = false;
            list = CollectionsKt.toList((Iterable)CollectionsKt.sortedWith((Iterable)$this$sortedBy$iv, (Comparator)new Comparator(){

                public final int compare(T a, T b) {
                    ManagedAccount it = (ManagedAccount)a;
                    boolean bl = false;
                    String string = it.getAlias();
                    Locale locale = Locale.US;
                    Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
                    String string2 = string.toLowerCase(locale);
                    Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                    it = (ManagedAccount)b;
                    Comparable comparable = (Comparable)((Object)string2);
                    bl = false;
                    string = it.getAlias();
                    Locale locale2 = Locale.US;
                    Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"US");
                    String string3 = string.toLowerCase(locale2);
                    Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
                    return ComparisonsKt.compareValues((Comparable)comparable, (Comparable)((Comparable)((Object)string3)));
                }
            }));
        }
        return list;
    }

    @NotNull
    public final String getStatusMessage() {
        return statusMessage;
    }

    public final boolean isBusy() {
        return busy;
    }

    @NotNull
    public final String getCurrentSessionName() {
        String string = mc.method_1548().method_1676();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getName(...)");
        return string;
    }

    @NotNull
    public final String getCurrentSessionUuid() {
        String string = mc.method_1548().method_44717().toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        return string;
    }

    public final boolean isCurrentAccount(@NotNull ManagedAccount account) {
        Intrinsics.checkNotNullParameter((Object)account, (String)"account");
        String currentUuid = this.getCurrentSessionUuid();
        return StringsKt.equals((String)account.getMinecraftUuid(), (String)currentUuid, (boolean)true) || StringsKt.equals((String)account.getMinecraftName(), (String)this.getCurrentSessionName(), (boolean)true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void login(@NotNull String alias) {
        Intrinsics.checkNotNullParameter((Object)alias, (String)"alias");
        String normalizedAlias = ((Object)StringsKt.trim((CharSequence)alias)).toString();
        if (((CharSequence)normalizedAlias).length() == 0) {
            this.notifyUser("Account Login", "Enter an alias first");
            return;
        }
        AccountManagerService accountManagerService = this;
        synchronized (accountManagerService) {
            boolean bl = false;
            if (busy) {
                INSTANCE.notifyUser("Account Login", "Another login is already running");
                return;
            }
            busy = true;
            statusMessage = "Logging into " + normalizedAlias + "...";
            Unit unit = Unit.INSTANCE;
        }
        ThreadsKt.thread$default((boolean)false, (boolean)true, null, (String)"Cobalt-AccountLogin", (int)0, () -> AccountManagerService.login$lambda$1(normalizedAlias), (int)21, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void remove(@NotNull String alias) {
        Intrinsics.checkNotNullParameter((Object)alias, (String)"alias");
        String normalizedAlias = ((Object)StringsKt.trim((CharSequence)alias)).toString();
        if (((CharSequence)normalizedAlias).length() == 0) {
            return;
        }
        AccountManagerService accountManagerService = this;
        synchronized (accountManagerService) {
            boolean bl = false;
            if (busy) {
                INSTANCE.notifyUser("Accounts", "Wait for the current login to finish");
                return;
            }
            boolean removed = store.getAccounts().removeIf(arg_0 -> AccountManagerService.remove$lambda$0$1(arg_0 -> AccountManagerService.remove$lambda$0$0(normalizedAlias, arg_0), arg_0));
            if (!removed) {
                return;
            }
            INSTANCE.saveLocked();
            logger.info("Removed stored account alias '{}'", (Object)normalizedAlias);
            Unit unit = Unit.INSTANCE;
        }
        this.notifyUser("Accounts", "Removed " + normalizedAlias);
    }

    /*
     * Unable to fully structure code
     */
    private final MinecraftSession authenticate(String alias, ManagedAccount existing) {
        if (existing == null || (var4_3 = existing.getRefreshToken()) == null) ** GOTO lbl-1000
        var6_4 = var4_3;
        it = var6_4;
        $i$a$-takeIf-AccountManagerService$authenticate$tokens$1 = false;
        v0 = var5_8 = !StringsKt.isBlank((CharSequence)it) != false ? var6_4 : null;
        if (var5_8 == null) ** GOTO lbl-1000
        refreshToken = var5_8;
        $i$a$-let-AccountManagerService$authenticate$tokens$2 = false;
        var10_10 = AccountManagerService.INSTANCE;
        try {
            $this$authenticate_u24lambda_u241_u240 = var10_10;
            $i$a$-runCatching-AccountManagerService$authenticate$tokens$2$1 = false;
            AccountManagerService.logger.info("Refreshing stored Microsoft session for alias '{}'", (Object)alias);
            $this$authenticate_u24lambda_u241_u240 = Result.constructor-impl((Object)super.refreshMicrosoftTokens(refreshToken));
        }
        catch (Throwable $i$a$-runCatching-AccountManagerService$authenticate$tokens$2$1) {
            $this$authenticate_u24lambda_u241_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)$i$a$-runCatching-AccountManagerService$authenticate$tokens$2$1));
        }
        var10_10 = $this$authenticate_u24lambda_u241_u240;
        v1 = Result.exceptionOrNull-impl((Object)var10_10);
        if (v1 == null) {
            v2 = var10_10;
        } else {
            exception = v1;
            $i$a$-getOrElse-AccountManagerService$authenticate$tokens$2$2 = false;
            AccountManagerService.logger.warn("Stored refresh token for alias '{}' failed, falling back to browser login", (Object)alias, (Object)exception);
            v2 = AccountManagerService.INSTANCE.startInteractiveMicrosoftLogin(alias);
        }
        var6_4 = (OAuthTokens)v2;
        if (var6_4 != null) {
            v3 = var6_4;
        } else lbl-1000:
        // 3 sources

        {
            v3 = this.startInteractiveMicrosoftLogin(alias);
        }
        tokens = v3;
        xblToken = this.requestXboxLiveToken(tokens.getAccessToken());
        xstsToken = this.requestXstsToken(xblToken.getToken());
        minecraftAccessToken = this.requestMinecraftAccessToken(xstsToken);
        profile = this.requestMinecraftProfile(minecraftAccessToken);
        return new MinecraftSession(minecraftAccessToken, tokens.getRefreshToken(), (String)profile.getFirst(), (String)profile.getSecond());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final OAuthTokens startInteractiveMicrosoftLogin(String alias) {
        byte[] verifierBytes = new byte[32];
        new SecureRandom().nextBytes(verifierBytes);
        String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(verifierBytes);
        Intrinsics.checkNotNull((Object)codeVerifier);
        String codeChallenge = this.base64UrlSha256(codeVerifier);
        HttpServer server = null;
        try {
            CompletableFuture codeFuture = new CompletableFuture();
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", 3000), 0);
            server.createContext("/", arg_0 -> AccountManagerService.startInteractiveMicrosoftLogin$lambda$0(codeFuture, arg_0));
            server.start();
            String authUrl = this.buildMicrosoftAuthUrl(codeChallenge);
            logger.info("Starting interactive Microsoft login for alias '{}'", (Object)alias);
            logger.info("Microsoft OAuth URL for alias '{}': {}", (Object)alias, (Object)authUrl);
            statusMessage = "Complete the Microsoft login for " + alias + " in your browser...";
            this.notifyUser("Microsoft Login", "Browser login started for " + alias);
            mc.execute(() -> AccountManagerService.startInteractiveMicrosoftLogin$lambda$1(authUrl));
            this.tryOpenBrowser(authUrl);
            String authCode = (String)codeFuture.get(5L, TimeUnit.MINUTES);
            Intrinsics.checkNotNull((Object)authCode);
            if (StringsKt.isBlank((CharSequence)authCode)) {
                throw new IllegalStateException("Microsoft login did not return an authorization code");
            }
            OAuthTokens oAuthTokens = this.exchangeAuthorizationCode(authCode, codeVerifier);
            return oAuthTokens;
        }
        finally {
            block4: {
                HttpServer httpServer = server;
                if (httpServer == null) break block4;
                httpServer.stop(0);
            }
        }
    }

    private final OAuthTokens refreshMicrosoftTokens(String refreshToken) {
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"client_id", (Object)MICROSOFT_CLIENT_ID), TuplesKt.to((Object)"scope", (Object)MICROSOFT_SCOPES), TuplesKt.to((Object)"redirect_uri", (Object)OAUTH_REDIRECT_URI), TuplesKt.to((Object)"grant_type", (Object)"refresh_token"), TuplesKt.to((Object)"refresh_token", (Object)refreshToken)};
        JsonObject response = this.postForm(OAUTH_TOKEN_URL, MapsKt.linkedMapOf((Pair[])pairArray));
        return new OAuthTokens(this.requiredString(response, "access_token"), this.requiredString(response, "refresh_token"));
    }

    private final OAuthTokens exchangeAuthorizationCode(String code, String codeVerifier) {
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"client_id", (Object)MICROSOFT_CLIENT_ID), TuplesKt.to((Object)"scope", (Object)MICROSOFT_SCOPES), TuplesKt.to((Object)"redirect_uri", (Object)OAUTH_REDIRECT_URI), TuplesKt.to((Object)"grant_type", (Object)"authorization_code"), TuplesKt.to((Object)"code", (Object)code), TuplesKt.to((Object)"code_verifier", (Object)codeVerifier)};
        JsonObject response = this.postForm(OAUTH_TOKEN_URL, MapsKt.linkedMapOf((Pair[])pairArray));
        return new OAuthTokens(this.requiredString(response, "access_token"), this.requiredString(response, "refresh_token"));
    }

    /*
     * WARNING - void declaration
     */
    private final XboxToken requestXboxLiveToken(String oauthAccessToken) {
        JsonArray jsonArray;
        void $this$requestXboxLiveToken_u24lambda_u240_u240;
        void $this$requestXboxLiveToken_u24lambda_u240;
        JsonObject jsonObject;
        JsonObject jsonObject2;
        JsonObject jsonObject3 = jsonObject2 = new JsonObject();
        String string = XBL_URL;
        AccountManagerService accountManagerService = this;
        boolean bl = false;
        JsonObject jsonObject4 = jsonObject = new JsonObject();
        String string2 = "Properties";
        void var9_11 = $this$requestXboxLiveToken_u24lambda_u240;
        boolean bl2 = false;
        $this$requestXboxLiveToken_u24lambda_u240_u240.addProperty("AuthMethod", "RPS");
        $this$requestXboxLiveToken_u24lambda_u240_u240.addProperty("SiteName", "user.auth.xboxlive.com");
        $this$requestXboxLiveToken_u24lambda_u240_u240.addProperty("RpsTicket", "d=" + oauthAccessToken);
        Unit unit = Unit.INSTANCE;
        var9_11.add(string2, (JsonElement)jsonObject);
        $this$requestXboxLiveToken_u24lambda_u240.addProperty("RelyingParty", "http://auth.xboxlive.com");
        $this$requestXboxLiveToken_u24lambda_u240.addProperty("TokenType", "JWT");
        Unit unit2 = Unit.INSTANCE;
        JsonObject response = accountManagerService.postJson(string, jsonObject2);
        jsonObject3 = response.getAsJsonObject("DisplayClaims");
        if (jsonObject3 == null || (jsonArray = jsonObject3.getAsJsonArray("xui")) == null || (jsonObject = (JsonElement)CollectionsKt.firstOrNull((Iterable)((Iterable)jsonArray))) == null || (jsonObject4 = jsonObject.getAsJsonObject()) == null || (string2 = this.requiredString(jsonObject4, "uhs")) == null) {
            throw new IllegalStateException("Xbox Live response did not include a user hash");
        }
        String userHash = string2;
        return new XboxToken(this.requiredString(response, "Token"), userHash);
    }

    /*
     * WARNING - void declaration
     */
    private final XboxToken requestXstsToken(String xboxToken) {
        JsonArray jsonArray;
        void $this$requestXstsToken_u24lambda_u240_u240_u240;
        JsonArray jsonArray2;
        void $this$requestXstsToken_u24lambda_u240_u240;
        void $this$requestXstsToken_u24lambda_u240;
        JsonObject jsonObject;
        JsonObject jsonObject2;
        JsonObject jsonObject3 = jsonObject2 = new JsonObject();
        String string = XSTS_URL;
        AccountManagerService accountManagerService = this;
        boolean bl = false;
        JsonObject jsonObject4 = jsonObject = new JsonObject();
        String string2 = "Properties";
        void var9_11 = $this$requestXstsToken_u24lambda_u240;
        boolean bl2 = false;
        $this$requestXstsToken_u24lambda_u240_u240.addProperty("SandboxId", "RETAIL");
        JsonArray jsonArray3 = jsonArray2 = new JsonArray();
        String string3 = "UserTokens";
        void var14_16 = $this$requestXstsToken_u24lambda_u240_u240;
        boolean bl3 = false;
        $this$requestXstsToken_u24lambda_u240_u240_u240.add(xboxToken);
        Unit unit = Unit.INSTANCE;
        var14_16.add(string3, (JsonElement)jsonArray2);
        Unit unit2 = Unit.INSTANCE;
        var9_11.add(string2, (JsonElement)jsonObject);
        $this$requestXstsToken_u24lambda_u240.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        $this$requestXstsToken_u24lambda_u240.addProperty("TokenType", "JWT");
        Unit unit3 = Unit.INSTANCE;
        JsonObject response = accountManagerService.postJson(string, jsonObject2);
        jsonObject3 = response.getAsJsonObject("DisplayClaims");
        if (jsonObject3 == null || (jsonArray = jsonObject3.getAsJsonArray("xui")) == null || (jsonObject = (JsonElement)CollectionsKt.firstOrNull((Iterable)((Iterable)jsonArray))) == null || (jsonObject4 = jsonObject.getAsJsonObject()) == null || (string2 = this.requiredString(jsonObject4, "uhs")) == null) {
            throw new IllegalStateException("XSTS response did not include a user hash");
        }
        String userHash = string2;
        return new XboxToken(this.requiredString(response, "Token"), userHash);
    }

    /*
     * WARNING - void declaration
     */
    private final String requestMinecraftAccessToken(XboxToken xstsToken) {
        void $this$requestMinecraftAccessToken_u24lambda_u240;
        JsonObject jsonObject;
        JsonObject jsonObject2 = jsonObject = new JsonObject();
        String string = MINECRAFT_AUTH_URL;
        AccountManagerService accountManagerService = this;
        boolean bl = false;
        $this$requestMinecraftAccessToken_u24lambda_u240.addProperty("identityToken", "XBL3.0 x=" + xstsToken.getUserHash() + ";" + xstsToken.getToken());
        Unit unit = Unit.INSTANCE;
        JsonObject response = accountManagerService.postJson(string, jsonObject);
        return this.requiredString(response, "access_token");
    }

    private final Pair<String, String> requestMinecraftProfile(String minecraftAccessToken) {
        JsonObject response = this.getJson(MINECRAFT_PROFILE_URL, minecraftAccessToken);
        return TuplesKt.to((Object)this.requiredString(response, "name"), (Object)this.requiredString(response, "id"));
    }

    private final void applySession(MinecraftSession session) {
        Object $this$applySession_u24lambda_u241;
        Object $this$applySession_u24lambda_u240;
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 minecraft = class_3102;
        MinecraftAccessor accessor = (MinecraftAccessor)minecraft;
        UUID profileId = this.parseUndashedUuid(session.getMinecraftUuid());
        class_320 user = new class_320(session.getMinecraftName(), profileId, session.getAccessToken(), Optional.empty(), Optional.empty());
        YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(minecraft.method_1487());
        Object object = this;
        try {
            $this$applySession_u24lambda_u240 = object;
            boolean bl = false;
            $this$applySession_u24lambda_u240 = Result.constructor-impl((Object)authService.createUserApiService(session.getAccessToken()));
        }
        catch (Throwable bl) {
            $this$applySession_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object = $this$applySession_u24lambda_u240;
        $this$applySession_u24lambda_u240 = UserApiService.OFFLINE;
        UserApiService userApiService = (UserApiService)(Result.isFailure-impl((Object)object) ? $this$applySession_u24lambda_u240 : object);
        $this$applySession_u24lambda_u240 = this;
        try {
            $this$applySession_u24lambda_u241 = (AccountManagerService)$this$applySession_u24lambda_u240;
            boolean bl = false;
            $this$applySession_u24lambda_u241 = Result.constructor-impl((Object)userApiService.fetchProperties());
        }
        catch (Throwable bl) {
            $this$applySession_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        $this$applySession_u24lambda_u240 = $this$applySession_u24lambda_u241;
        $this$applySession_u24lambda_u241 = UserApiService.OFFLINE_PROPERTIES;
        UserApiService.UserProperties userProperties = (UserApiService.UserProperties)(Result.isFailure-impl((Object)$this$applySession_u24lambda_u240) ? $this$applySession_u24lambda_u241 : $this$applySession_u24lambda_u240);
        class_7497 class_74972 = class_7497.method_44143((YggdrasilAuthenticationService)authService, (File)minecraft.field_1697);
        Intrinsics.checkNotNullExpressionValue((Object)class_74972, (String)"create(...)");
        class_7497 services = class_74972;
        CompletableFuture<ProfileResult> profileFuture = CompletableFuture.completedFuture(new ProfileResult(new GameProfile(profileId, session.getMinecraftName())));
        CompletableFuture<UserApiService.UserProperties> userPropertiesFuture = CompletableFuture.completedFuture(userProperties);
        accessor.setUser(user);
        accessor.setProfileFuture(profileFuture);
        accessor.setUserApiService(userApiService);
        accessor.setUserPropertiesFuture(userPropertiesFuture);
        accessor.setProfileKeyPairManager(class_7853.field_40800);
        accessor.setServices(services);
        accessor.setReportingContext(class_7574.method_44599((class_7569)class_7569.method_44586(), (UserApiService)userApiService));
        minecraft.method_38932();
        if (minecraft.field_1687 != null) {
            minecraft.method_72100();
        }
    }

    private final JsonObject postForm(String url, Map<String, String> params) {
        String formBody = CollectionsKt.joinToString$default((Iterable)params.entrySet(), (CharSequence)"&", null, null, (int)0, null, AccountManagerService::postForm$lambda$0, (int)30, null);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30L)).header("Content-Type", "application/x-www-form-urlencoded").header("Accept", "application/json").header("User-Agent", USER_AGENT).POST(HttpRequest.BodyPublishers.ofString(formBody)).build();
        Intrinsics.checkNotNull((Object)request);
        return this.sendJson(request);
    }

    private final JsonObject postJson(String url, JsonObject body) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30L)).header("Content-Type", "application/json").header("Accept", "application/json").header("User-Agent", USER_AGENT).POST(HttpRequest.BodyPublishers.ofString(gson.toJson((JsonElement)body))).build();
        Intrinsics.checkNotNull((Object)request);
        return this.sendJson(request);
    }

    private final JsonObject getJson(String url, String bearerToken) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30L)).header("Accept", "application/json").header("User-Agent", USER_AGENT).header("Authorization", "Bearer " + bearerToken).GET().build();
        Intrinsics.checkNotNull((Object)request);
        return this.sendJson(request);
    }

    private final JsonObject sendJson(HttpRequest request) {
        JsonObject jsonObject;
        String body;
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String string = response.body();
        if (string == null) {
            string = "";
        }
        if (!StringsKt.isBlank((CharSequence)(body = string))) {
            Object object;
            Object object2 = this;
            try {
                AccountManagerService $this$sendJson_u24lambda_u240 = object2;
                boolean bl = false;
                object = Result.constructor-impl((Object)JsonParser.parseString((String)body).getAsJsonObject());
            }
            catch (Throwable throwable) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object2 = object;
            jsonObject = (JsonObject)(Result.isFailure-impl((Object)object2) ? null : object2);
        } else {
            jsonObject = null;
        }
        JsonObject parsed = jsonObject;
        int n = response.statusCode();
        if (!(200 <= n ? n < 300 : false)) {
            Object object = parsed;
            if (object == null || (object = this.bestErrorMessage((JsonObject)object)) == null) {
                object = "HTTP " + response.statusCode() + " from " + request.uri();
            }
            throw new IllegalStateException((String)object);
        }
        JsonObject jsonObject2 = parsed;
        if (jsonObject2 == null) {
            throw new IllegalStateException("Empty response from " + request.uri());
        }
        return jsonObject2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void load() {
        AccountManagerService accountManagerService = this;
        synchronized (accountManagerService) {
            Object object;
            Object $this$load_u24lambda_u240_u240;
            boolean bl = false;
            if (!storeFile.exists()) {
                File file = storeFile.getParentFile();
                if (file != null) {
                    file.mkdirs();
                }
                store = new AccountStore(null, 1, null);
                return;
            }
            Object object2 = INSTANCE;
            try {
                $this$load_u24lambda_u240_u240 = object2;
                boolean bl2 = false;
                Charset charset = StandardCharsets.UTF_8;
                Intrinsics.checkNotNullExpressionValue((Object)charset, (String)"UTF_8");
                AccountStore accountStore = (AccountStore)gson.fromJson(FilesKt.readText((File)storeFile, (Charset)charset), AccountStore.class);
                if (accountStore == null) {
                    accountStore = new AccountStore(null, 1, null);
                }
                $this$load_u24lambda_u240_u240 = Result.constructor-impl((Object)accountStore);
            }
            catch (Throwable bl2) {
                $this$load_u24lambda_u240_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl2));
            }
            object2 = $this$load_u24lambda_u240_u240;
            Throwable throwable = Result.exceptionOrNull-impl((Object)object2);
            if (throwable == null) {
                object = object2;
            } else {
                Throwable exception = throwable;
                boolean bl3 = false;
                logger.error("Failed to load account store from {}", (Object)storeFile.getAbsolutePath(), (Object)exception);
                object = new AccountStore(null, 1, null);
            }
            store = (AccountStore)object;
            Unit unit = Unit.INSTANCE;
        }
    }

    private final void saveLocked() {
        File file = storeFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        String string = gson.toJson((Object)store);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toJson(...)");
        Charset charset = StandardCharsets.UTF_8;
        Intrinsics.checkNotNullExpressionValue((Object)charset, (String)"UTF_8");
        FilesKt.writeText((File)storeFile, (String)string, (Charset)charset);
    }

    private final void upsertAccount(ManagedAccount account) {
        int existingIndex;
        block4: {
            int n;
            List<ManagedAccount> $this$indexOfFirst$iv = store.getAccounts();
            boolean $i$f$indexOfFirst = false;
            int index$iv = 0;
            Iterator<ManagedAccount> iterator = $this$indexOfFirst$iv.iterator();
            while (iterator.hasNext()) {
                ManagedAccount item$iv;
                ManagedAccount it = item$iv = iterator.next();
                boolean bl = false;
                if (StringsKt.equals((String)it.getAlias(), (String)account.getAlias(), (boolean)true)) {
                    n = index$iv;
                    break block4;
                }
                ++index$iv;
            }
            n = existingIndex = -1;
        }
        if (existingIndex >= 0) {
            store.getAccounts().set(existingIndex, account);
        } else {
            store.getAccounts().add(account);
        }
    }

    private final String buildMicrosoftAuthUrl(String codeChallenge) {
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"client_id", (Object)MICROSOFT_CLIENT_ID), TuplesKt.to((Object)"response_type", (Object)"code"), TuplesKt.to((Object)"redirect_uri", (Object)OAUTH_REDIRECT_URI), TuplesKt.to((Object)"scope", (Object)MICROSOFT_SCOPES), TuplesKt.to((Object)"prompt", (Object)"select_account"), TuplesKt.to((Object)"code_challenge", (Object)codeChallenge), TuplesKt.to((Object)"code_challenge_method", (Object)"S256")};
        LinkedHashMap params = MapsKt.linkedMapOf((Pair[])pairArray);
        Set set = params.entrySet();
        Intrinsics.checkNotNullExpressionValue(set, (String)"<get-entries>(...)");
        return "https://login.live.com/oauth20_authorize.srf?" + CollectionsKt.joinToString$default((Iterable)set, (CharSequence)"&", null, null, (int)0, null, AccountManagerService::buildMicrosoftAuthUrl$lambda$0, (int)30, null);
    }

    private final void tryOpenBrowser(String url) {
        block3: {
            Object object;
            Object object2 = this;
            try {
                AccountManagerService $this$tryOpenBrowser_u24lambda_u240 = object2;
                boolean bl = false;
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI.create(url));
                }
                object = Result.constructor-impl((Object)Unit.INSTANCE);
            }
            catch (Throwable bl) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            object2 = object;
            Throwable throwable = Result.exceptionOrNull-impl((Object)object2);
            if (throwable == null) break block3;
            Object exception = object = throwable;
            boolean bl = false;
            logger.warn("Failed to open the Microsoft login URL automatically", (Throwable)exception);
            INSTANCE.notifyUser("Microsoft Login", "URL copied to clipboard");
        }
    }

    private final void notifyUser(String title, String description) {
        mc.execute(() -> AccountManagerService.notifyUser$lambda$0(title, description));
    }

    /*
     * WARNING - void declaration
     */
    private final Map<String, String> parseQuery(String rawQuery) {
        void $this$mapNotNullTo$iv$iv;
        if (StringsKt.isBlank((CharSequence)rawQuery)) {
            return MapsKt.emptyMap();
        }
        String[] stringArray = new String[]{"&"};
        Iterable $this$mapNotNull$iv = StringsKt.split$default((CharSequence)rawQuery, (String[])stringArray, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            Pair pair;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            String part = (String)element$iv$iv;
            boolean bl2 = false;
            int separatorIndex = StringsKt.indexOf$default((CharSequence)part, (char)'=', (int)0, (boolean)false, (int)6, null);
            if (separatorIndex < 0) {
                pair = null;
            } else {
                String string = part.substring(0, separatorIndex);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
                String key = URLDecoder.decode(string, StandardCharsets.UTF_8);
                String string2 = part.substring(separatorIndex + 1);
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
                String value = URLDecoder.decode(string2, StandardCharsets.UTF_8);
                pair = TuplesKt.to((Object)key, (Object)value);
            }
            if (pair == null) continue;
            Pair it$iv$iv = pair;
            boolean bl3 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        return MapsKt.toMap((Iterable)((List)destination$iv$iv));
    }

    private final UUID parseUndashedUuid(String raw) {
        String string;
        String normalized = ((Object)StringsKt.trim((CharSequence)raw)).toString();
        if (StringsKt.contains$default((CharSequence)normalized, (char)'-', (boolean)false, (int)2, null)) {
            string = normalized;
        } else {
            CharSequence charSequence = normalized;
            Regex regex = UUID_DASH_PATTERN;
            String string2 = "$1-$2-$3-$4-$5";
            string = regex.replaceFirst(charSequence, string2);
        }
        String dashed = string;
        UUID uUID = UUID.fromString(dashed);
        Intrinsics.checkNotNullExpressionValue((Object)uUID, (String)"fromString(...)");
        return uUID;
    }

    private final String base64UrlSha256(String value) {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        String string = value;
        Charset charset = StandardCharsets.UTF_8;
        Intrinsics.checkNotNullExpressionValue((Object)charset, (String)"UTF_8");
        byte[] byArray = string.getBytes(charset);
        Intrinsics.checkNotNullExpressionValue((Object)byArray, (String)"getBytes(...)");
        byte[] digest = messageDigest.digest(byArray);
        String string2 = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"encodeToString(...)");
        return string2;
    }

    private final String urlEncode(String value) {
        String string = URLEncoder.encode(value, StandardCharsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"encode(...)");
        return string;
    }

    private final String requiredString(JsonObject $this$requiredString, String key) {
        Object object = $this$requiredString.get(key);
        if (object == null || (object = object.getAsString()) == null) {
            throw new IllegalStateException("Missing '" + key + "' in response");
        }
        return object;
    }

    /*
     * WARNING - void declaration
     */
    private final String bestErrorMessage(JsonObject $this$bestErrorMessage) {
        void $this$mapNotNullTo$iv$iv;
        Object[] objectArray = new String[]{"error_description", "errorMessage", "message", "Message", "error"};
        Iterable $this$mapNotNull$iv = CollectionsKt.listOf((Object[])objectArray);
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            String it$iv$iv;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            String key = (String)element$iv$iv;
            boolean bl2 = false;
            JsonElement jsonElement = $this$bestErrorMessage.get(key);
            if ((jsonElement != null ? jsonElement.getAsString() : null) == null) continue;
            it$iv$iv = it$iv$iv;
            boolean bl3 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        String string = (String)CollectionsKt.firstOrNull((List)((List)destination$iv$iv));
        if (string == null) {
            String string2 = $this$bestErrorMessage.toString();
            string = string2;
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toString(...)");
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void login$lambda$1$2(MinecraftSession $session, String $normalizedAlias) {
        try {
            INSTANCE.applySession($session);
            logger.info("Account '{}' session swap completed successfully for '{}'", (Object)$normalizedAlias, (Object)$session.getMinecraftName());
            INSTANCE.notifyUser("Account Login", "Now using " + $session.getMinecraftName());
            statusMessage = "Logged in as " + $session.getMinecraftName();
        }
        catch (Exception exception) {
            logger.error("Account '{}' authenticated but session swap failed", (Object)$normalizedAlias, (Object)exception);
            String string = exception.getMessage();
            if (string == null || (string = StringsKt.take((String)string, (int)96)) == null) {
                string = "Session swap failed";
            }
            INSTANCE.notifyUser("Account Login Failed", string);
            statusMessage = "Session swap failed";
        }
        finally {
            busy = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit login$lambda$1(String $normalizedAlias) {
        try {
            Object object;
            logger.info("Account login requested for alias '{}'", (Object)$normalizedAlias);
            Object[] objectArray = INSTANCE;
            synchronized (objectArray) {
                Object v0;
                block11: {
                    boolean bl = false;
                    Iterable $this$firstOrNull$iv = store.getAccounts();
                    boolean $i$f$firstOrNull = false;
                    for (Object element$iv : $this$firstOrNull$iv) {
                        ManagedAccount it = (ManagedAccount)element$iv;
                        boolean bl2 = false;
                        if (!StringsKt.equals((String)it.getAlias(), (String)$normalizedAlias, (boolean)true)) continue;
                        v0 = element$iv;
                        break block11;
                    }
                    v0 = null;
                }
                object = v0;
            }
            ManagedAccount existing = object;
            MinecraftSession session = INSTANCE.authenticate($normalizedAlias, existing);
            object = INSTANCE;
            synchronized (object) {
                boolean bl = false;
                Object object2 = existing;
                if (object2 == null || (object2 = ((ManagedAccount)object2).getAlias()) == null) {
                    object2 = $normalizedAlias;
                }
                INSTANCE.upsertAccount(new ManagedAccount((String)object2, session.getRefreshToken(), session.getMinecraftName(), session.getMinecraftUuid(), System.currentTimeMillis()));
                INSTANCE.saveLocked();
                Unit unit = Unit.INSTANCE;
            }
            objectArray = new Object[]{$normalizedAlias, session.getMinecraftName(), session.getMinecraftUuid()};
            logger.info("Account '{}' authenticated successfully as '{}' ({})", objectArray);
            statusMessage = "Switching session to " + session.getMinecraftName() + "...";
            mc.execute(() -> AccountManagerService.login$lambda$1$2(session, $normalizedAlias));
        }
        catch (Exception exception) {
            logger.error("Account '{}' login failed", (Object)$normalizedAlias, (Object)exception);
            String string = exception.getMessage();
            if (string == null || (string = StringsKt.take((String)string, (int)96)) == null) {
                string = "Login failed";
            }
            INSTANCE.notifyUser("Account Login Failed", string);
            statusMessage = "Login failed";
            busy = false;
        }
        return Unit.INSTANCE;
    }

    private static final boolean remove$lambda$0$0(String $normalizedAlias, ManagedAccount it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return StringsKt.equals((String)it.getAlias(), (String)$normalizedAlias, (boolean)true);
    }

    private static final boolean remove$lambda$0$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void startInteractiveMicrosoftLogin$lambda$0(CompletableFuture $codeFuture, HttpExchange exchange) {
        String string;
        Map<String, String> query;
        String string2 = exchange.getRequestURI().getRawQuery();
        if (string2 == null) {
            string2 = "";
        }
        if ((query = INSTANCE.parseQuery(string2)).containsKey("error")) {
            String string3 = query.get("error_description");
            if (string3 == null && (string3 = query.get("error")) == null) {
                string3 = "Microsoft login was denied";
            }
            $codeFuture.completeExceptionally(new IllegalStateException(string3));
            string = FAILURE_HTML;
        } else {
            String string4 = query.get("code");
            if (string4 == null) {
                string4 = "";
            }
            $codeFuture.complete(string4);
            string = SUCCESS_HTML;
        }
        String responseHtml = string;
        Object object = responseHtml;
        Charset charset = StandardCharsets.UTF_8;
        Intrinsics.checkNotNullExpressionValue((Object)charset, (String)"UTF_8");
        byte[] byArray = ((String)object).getBytes(charset);
        Intrinsics.checkNotNullExpressionValue((Object)byArray, (String)"getBytes(...)");
        byte[] bytes = byArray;
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        object = exchange.getResponseBody();
        Throwable throwable = null;
        try {
            OutputStream it = (OutputStream)object;
            boolean bl = false;
            it.write(bytes);
            Unit unit = Unit.INSTANCE;
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            CloseableKt.closeFinally((Closeable)object, (Throwable)throwable);
        }
    }

    private static final void startInteractiveMicrosoftLogin$lambda$1(String $authUrl) {
        AccountManagerService.mc.field_1774.method_1455($authUrl);
    }

    private static final CharSequence postForm$lambda$0(Map.Entry entry) {
        Intrinsics.checkNotNullParameter((Object)entry, (String)"<destruct>");
        String key = (String)entry.getKey();
        String value = (String)entry.getValue();
        return INSTANCE.urlEncode(key) + "=" + INSTANCE.urlEncode(value);
    }

    private static final CharSequence buildMicrosoftAuthUrl$lambda$0(Map.Entry it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        Object k = it.getKey();
        Intrinsics.checkNotNullExpressionValue(k, (String)"<get-key>(...)");
        String string = INSTANCE.urlEncode((String)k);
        Object v = it.getValue();
        Intrinsics.checkNotNullExpressionValue(v, (String)"<get-value>(...)");
        return string + "=" + INSTANCE.urlEncode((String)v);
    }

    private static final void notifyUser$lambda$0(String $title, String $description) {
        NotificationManager.queue$default(NotificationManager.INSTANCE, $title, $description, 0L, 4, null);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        logger = LoggerFactory.getLogger((String)"Cobalt/AccountManager");
        gson = new GsonBuilder().setPrettyPrinting().create();
        httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(15L)).build();
        storeFile = new File(AccountManagerService.mc.field_1697, "config/cobalt/account_manager.json");
        statusMessage = "Idle";
        store = new AccountStore(null, 1, null);
        INSTANCE.load();
        UUID_DASH_PATTERN = new Regex("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})");
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ \u0010\t\u001a\u00020\u00002\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\t\u0010\nJ\u001b\u0010\r\u001a\u00020\f2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0011\u0010\u0010\u001a\u00020\u000fH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014R\u001d\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0015\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/account/AccountManagerService$AccountStore;", "", "", "Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;", "accounts", "<init>", "(Ljava/util/List;)V", "component1", "()Ljava/util/List;", "copy", "(Ljava/util/List;)Lorg/cobalt/internal/account/AccountManagerService$AccountStore;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Ljava/util/List;", "getAccounts", "cobalt"})
    private static final class AccountStore {
        @NotNull
        private final List<ManagedAccount> accounts;

        public AccountStore(@NotNull List<ManagedAccount> accounts) {
            Intrinsics.checkNotNullParameter(accounts, (String)"accounts");
            this.accounts = accounts;
        }

        public /* synthetic */ AccountStore(List list, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 1) != 0) {
                list = new ArrayList();
            }
            this(list);
        }

        @NotNull
        public final List<ManagedAccount> getAccounts() {
            return this.accounts;
        }

        @NotNull
        public final List<ManagedAccount> component1() {
            return this.accounts;
        }

        @NotNull
        public final AccountStore copy(@NotNull List<ManagedAccount> accounts) {
            Intrinsics.checkNotNullParameter(accounts, (String)"accounts");
            return new AccountStore(accounts);
        }

        public static /* synthetic */ AccountStore copy$default(AccountStore accountStore, List list, int n, Object object) {
            if ((n & 1) != 0) {
                list = accountStore.accounts;
            }
            return accountStore.copy(list);
        }

        @NotNull
        public String toString() {
            return "AccountStore(accounts=" + this.accounts + ")";
        }

        public int hashCode() {
            return ((Object)this.accounts).hashCode();
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AccountStore)) {
                return false;
            }
            AccountStore accountStore = (AccountStore)other;
            return Intrinsics.areEqual(this.accounts, accountStore.accounts);
        }

        public AccountStore() {
            this(null, 1, null);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0002\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\fJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\fJ\u0010\u0010\u0010\u001a\u00020\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011JB\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001b\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\fR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001c\u001a\u0004\b\u001e\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001c\u001a\u0004\b\u001f\u0010\fR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001c\u001a\u0004\b \u0010\fR\u0017\u0010\b\u001a\u00020\u00078\u0006\u00a2\u0006\f\n\u0004\b\b\u0010!\u001a\u0004\b\"\u0010\u0011\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;", "", "", "alias", "refreshToken", "minecraftName", "minecraftUuid", "", "lastLoginAt", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V", "component1", "()Ljava/lang/String;", "component2", "component3", "component4", "component5", "()J", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)Lorg/cobalt/internal/account/AccountManagerService$ManagedAccount;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getAlias", "getRefreshToken", "getMinecraftName", "getMinecraftUuid", "J", "getLastLoginAt", "cobalt"})
    public static final class ManagedAccount {
        @NotNull
        private final String alias;
        @NotNull
        private final String refreshToken;
        @NotNull
        private final String minecraftName;
        @NotNull
        private final String minecraftUuid;
        private final long lastLoginAt;

        public ManagedAccount(@NotNull String alias, @NotNull String refreshToken, @NotNull String minecraftName, @NotNull String minecraftUuid, long lastLoginAt) {
            Intrinsics.checkNotNullParameter((Object)alias, (String)"alias");
            Intrinsics.checkNotNullParameter((Object)refreshToken, (String)"refreshToken");
            Intrinsics.checkNotNullParameter((Object)minecraftName, (String)"minecraftName");
            Intrinsics.checkNotNullParameter((Object)minecraftUuid, (String)"minecraftUuid");
            this.alias = alias;
            this.refreshToken = refreshToken;
            this.minecraftName = minecraftName;
            this.minecraftUuid = minecraftUuid;
            this.lastLoginAt = lastLoginAt;
        }

        public /* synthetic */ ManagedAccount(String string, String string2, String string3, String string4, long l, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 4) != 0) {
                string3 = "";
            }
            if ((n & 8) != 0) {
                string4 = "";
            }
            if ((n & 0x10) != 0) {
                l = 0L;
            }
            this(string, string2, string3, string4, l);
        }

        @NotNull
        public final String getAlias() {
            return this.alias;
        }

        @NotNull
        public final String getRefreshToken() {
            return this.refreshToken;
        }

        @NotNull
        public final String getMinecraftName() {
            return this.minecraftName;
        }

        @NotNull
        public final String getMinecraftUuid() {
            return this.minecraftUuid;
        }

        public final long getLastLoginAt() {
            return this.lastLoginAt;
        }

        @NotNull
        public final String component1() {
            return this.alias;
        }

        @NotNull
        public final String component2() {
            return this.refreshToken;
        }

        @NotNull
        public final String component3() {
            return this.minecraftName;
        }

        @NotNull
        public final String component4() {
            return this.minecraftUuid;
        }

        public final long component5() {
            return this.lastLoginAt;
        }

        @NotNull
        public final ManagedAccount copy(@NotNull String alias, @NotNull String refreshToken, @NotNull String minecraftName, @NotNull String minecraftUuid, long lastLoginAt) {
            Intrinsics.checkNotNullParameter((Object)alias, (String)"alias");
            Intrinsics.checkNotNullParameter((Object)refreshToken, (String)"refreshToken");
            Intrinsics.checkNotNullParameter((Object)minecraftName, (String)"minecraftName");
            Intrinsics.checkNotNullParameter((Object)minecraftUuid, (String)"minecraftUuid");
            return new ManagedAccount(alias, refreshToken, minecraftName, minecraftUuid, lastLoginAt);
        }

        public static /* synthetic */ ManagedAccount copy$default(ManagedAccount managedAccount, String string, String string2, String string3, String string4, long l, int n, Object object) {
            if ((n & 1) != 0) {
                string = managedAccount.alias;
            }
            if ((n & 2) != 0) {
                string2 = managedAccount.refreshToken;
            }
            if ((n & 4) != 0) {
                string3 = managedAccount.minecraftName;
            }
            if ((n & 8) != 0) {
                string4 = managedAccount.minecraftUuid;
            }
            if ((n & 0x10) != 0) {
                l = managedAccount.lastLoginAt;
            }
            return managedAccount.copy(string, string2, string3, string4, l);
        }

        @NotNull
        public String toString() {
            return "ManagedAccount(alias=" + this.alias + ", refreshToken=" + this.refreshToken + ", minecraftName=" + this.minecraftName + ", minecraftUuid=" + this.minecraftUuid + ", lastLoginAt=" + this.lastLoginAt + ")";
        }

        public int hashCode() {
            int result = this.alias.hashCode();
            result = result * 31 + this.refreshToken.hashCode();
            result = result * 31 + this.minecraftName.hashCode();
            result = result * 31 + this.minecraftUuid.hashCode();
            result = result * 31 + Long.hashCode(this.lastLoginAt);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ManagedAccount)) {
                return false;
            }
            ManagedAccount managedAccount = (ManagedAccount)other;
            if (!Intrinsics.areEqual((Object)this.alias, (Object)managedAccount.alias)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.refreshToken, (Object)managedAccount.refreshToken)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.minecraftName, (Object)managedAccount.minecraftName)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.minecraftUuid, (Object)managedAccount.minecraftUuid)) {
                return false;
            }
            return this.lastLoginAt == managedAccount.lastLoginAt;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\t\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\nJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\nJ8\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0015\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0011\u0010\u0017\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0017\u0010\nR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0018\u001a\u0004\b\u001a\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u001b\u0010\nR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0018\u001a\u0004\b\u001c\u0010\n\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/internal/account/AccountManagerService$MinecraftSession;", "", "", "accessToken", "refreshToken", "minecraftName", "minecraftUuid", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "component2", "component3", "component4", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/cobalt/internal/account/AccountManagerService$MinecraftSession;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getAccessToken", "getRefreshToken", "getMinecraftName", "getMinecraftUuid", "cobalt"})
    private static final class MinecraftSession {
        @NotNull
        private final String accessToken;
        @NotNull
        private final String refreshToken;
        @NotNull
        private final String minecraftName;
        @NotNull
        private final String minecraftUuid;

        public MinecraftSession(@NotNull String accessToken, @NotNull String refreshToken, @NotNull String minecraftName, @NotNull String minecraftUuid) {
            Intrinsics.checkNotNullParameter((Object)accessToken, (String)"accessToken");
            Intrinsics.checkNotNullParameter((Object)refreshToken, (String)"refreshToken");
            Intrinsics.checkNotNullParameter((Object)minecraftName, (String)"minecraftName");
            Intrinsics.checkNotNullParameter((Object)minecraftUuid, (String)"minecraftUuid");
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.minecraftName = minecraftName;
            this.minecraftUuid = minecraftUuid;
        }

        @NotNull
        public final String getAccessToken() {
            return this.accessToken;
        }

        @NotNull
        public final String getRefreshToken() {
            return this.refreshToken;
        }

        @NotNull
        public final String getMinecraftName() {
            return this.minecraftName;
        }

        @NotNull
        public final String getMinecraftUuid() {
            return this.minecraftUuid;
        }

        @NotNull
        public final String component1() {
            return this.accessToken;
        }

        @NotNull
        public final String component2() {
            return this.refreshToken;
        }

        @NotNull
        public final String component3() {
            return this.minecraftName;
        }

        @NotNull
        public final String component4() {
            return this.minecraftUuid;
        }

        @NotNull
        public final MinecraftSession copy(@NotNull String accessToken, @NotNull String refreshToken, @NotNull String minecraftName, @NotNull String minecraftUuid) {
            Intrinsics.checkNotNullParameter((Object)accessToken, (String)"accessToken");
            Intrinsics.checkNotNullParameter((Object)refreshToken, (String)"refreshToken");
            Intrinsics.checkNotNullParameter((Object)minecraftName, (String)"minecraftName");
            Intrinsics.checkNotNullParameter((Object)minecraftUuid, (String)"minecraftUuid");
            return new MinecraftSession(accessToken, refreshToken, minecraftName, minecraftUuid);
        }

        public static /* synthetic */ MinecraftSession copy$default(MinecraftSession minecraftSession, String string, String string2, String string3, String string4, int n, Object object) {
            if ((n & 1) != 0) {
                string = minecraftSession.accessToken;
            }
            if ((n & 2) != 0) {
                string2 = minecraftSession.refreshToken;
            }
            if ((n & 4) != 0) {
                string3 = minecraftSession.minecraftName;
            }
            if ((n & 8) != 0) {
                string4 = minecraftSession.minecraftUuid;
            }
            return minecraftSession.copy(string, string2, string3, string4);
        }

        @NotNull
        public String toString() {
            return "MinecraftSession(accessToken=" + this.accessToken + ", refreshToken=" + this.refreshToken + ", minecraftName=" + this.minecraftName + ", minecraftUuid=" + this.minecraftUuid + ")";
        }

        public int hashCode() {
            int result = this.accessToken.hashCode();
            result = result * 31 + this.refreshToken.hashCode();
            result = result * 31 + this.minecraftName.hashCode();
            result = result * 31 + this.minecraftUuid.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MinecraftSession)) {
                return false;
            }
            MinecraftSession minecraftSession = (MinecraftSession)other;
            if (!Intrinsics.areEqual((Object)this.accessToken, (Object)minecraftSession.accessToken)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.refreshToken, (Object)minecraftSession.refreshToken)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.minecraftName, (Object)minecraftSession.minecraftName)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.minecraftUuid, (Object)minecraftSession.minecraftUuid);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0011\u001a\u00020\u0010H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0013\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/account/AccountManagerService$OAuthTokens;", "", "", "accessToken", "refreshToken", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "component2", "copy", "(Ljava/lang/String;Ljava/lang/String;)Lorg/cobalt/internal/account/AccountManagerService$OAuthTokens;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getAccessToken", "getRefreshToken", "cobalt"})
    private static final class OAuthTokens {
        @NotNull
        private final String accessToken;
        @NotNull
        private final String refreshToken;

        public OAuthTokens(@NotNull String accessToken, @NotNull String refreshToken) {
            Intrinsics.checkNotNullParameter((Object)accessToken, (String)"accessToken");
            Intrinsics.checkNotNullParameter((Object)refreshToken, (String)"refreshToken");
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        @NotNull
        public final String getAccessToken() {
            return this.accessToken;
        }

        @NotNull
        public final String getRefreshToken() {
            return this.refreshToken;
        }

        @NotNull
        public final String component1() {
            return this.accessToken;
        }

        @NotNull
        public final String component2() {
            return this.refreshToken;
        }

        @NotNull
        public final OAuthTokens copy(@NotNull String accessToken, @NotNull String refreshToken) {
            Intrinsics.checkNotNullParameter((Object)accessToken, (String)"accessToken");
            Intrinsics.checkNotNullParameter((Object)refreshToken, (String)"refreshToken");
            return new OAuthTokens(accessToken, refreshToken);
        }

        public static /* synthetic */ OAuthTokens copy$default(OAuthTokens oAuthTokens, String string, String string2, int n, Object object) {
            if ((n & 1) != 0) {
                string = oAuthTokens.accessToken;
            }
            if ((n & 2) != 0) {
                string2 = oAuthTokens.refreshToken;
            }
            return oAuthTokens.copy(string, string2);
        }

        @NotNull
        public String toString() {
            return "OAuthTokens(accessToken=" + this.accessToken + ", refreshToken=" + this.refreshToken + ")";
        }

        public int hashCode() {
            int result = this.accessToken.hashCode();
            result = result * 31 + this.refreshToken.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof OAuthTokens)) {
                return false;
            }
            OAuthTokens oAuthTokens = (OAuthTokens)other;
            if (!Intrinsics.areEqual((Object)this.accessToken, (Object)oAuthTokens.accessToken)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.refreshToken, (Object)oAuthTokens.refreshToken);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0011\u001a\u00020\u0010H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0013\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/account/AccountManagerService$XboxToken;", "", "", "token", "userHash", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "component2", "copy", "(Ljava/lang/String;Ljava/lang/String;)Lorg/cobalt/internal/account/AccountManagerService$XboxToken;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getToken", "getUserHash", "cobalt"})
    private static final class XboxToken {
        @NotNull
        private final String token;
        @NotNull
        private final String userHash;

        public XboxToken(@NotNull String token, @NotNull String userHash) {
            Intrinsics.checkNotNullParameter((Object)token, (String)"token");
            Intrinsics.checkNotNullParameter((Object)userHash, (String)"userHash");
            this.token = token;
            this.userHash = userHash;
        }

        @NotNull
        public final String getToken() {
            return this.token;
        }

        @NotNull
        public final String getUserHash() {
            return this.userHash;
        }

        @NotNull
        public final String component1() {
            return this.token;
        }

        @NotNull
        public final String component2() {
            return this.userHash;
        }

        @NotNull
        public final XboxToken copy(@NotNull String token, @NotNull String userHash) {
            Intrinsics.checkNotNullParameter((Object)token, (String)"token");
            Intrinsics.checkNotNullParameter((Object)userHash, (String)"userHash");
            return new XboxToken(token, userHash);
        }

        public static /* synthetic */ XboxToken copy$default(XboxToken xboxToken, String string, String string2, int n, Object object) {
            if ((n & 1) != 0) {
                string = xboxToken.token;
            }
            if ((n & 2) != 0) {
                string2 = xboxToken.userHash;
            }
            return xboxToken.copy(string, string2);
        }

        @NotNull
        public String toString() {
            return "XboxToken(token=" + this.token + ", userHash=" + this.userHash + ")";
        }

        public int hashCode() {
            int result = this.token.hashCode();
            result = result * 31 + this.userHash.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof XboxToken)) {
                return false;
            }
            XboxToken xboxToken = (XboxToken)other;
            if (!Intrinsics.areEqual((Object)this.token, (Object)xboxToken.token)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.userHash, (Object)xboxToken.userHash);
        }
    }
}

