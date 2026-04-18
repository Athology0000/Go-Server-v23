/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.valueproviders.ConstantFloat
 *  net.minecraft.util.valueproviders.FloatProvider
 *  net.minecraft.util.valueproviders.SampledFloat
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.resources.sounds;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.SampledFloat;
import org.apache.commons.lang3.Validate;

@Environment(value=EnvType.CLIENT)
public class SoundEventRegistrationSerializer
implements JsonDeserializer<SoundEventRegistration> {
    private static final FloatProvider DEFAULT_FLOAT = ConstantFloat.of((float)1.0f);

    public SoundEventRegistration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = GsonHelper.convertToJsonObject((JsonElement)jsonElement, (String)"entry");
        boolean bl = GsonHelper.getAsBoolean((JsonObject)jsonObject, (String)"replace", (boolean)false);
        String string = GsonHelper.getAsString((JsonObject)jsonObject, (String)"subtitle", null);
        List<Sound> list = this.getSounds(jsonObject);
        return new SoundEventRegistration(list, bl, string);
    }

    private List<Sound> getSounds(JsonObject jsonObject) {
        ArrayList list = Lists.newArrayList();
        if (jsonObject.has("sounds")) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray((JsonObject)jsonObject, (String)"sounds");
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonElement jsonElement = jsonArray.get(i);
                if (GsonHelper.isStringValue((JsonElement)jsonElement)) {
                    Identifier identifier = Identifier.parse((String)GsonHelper.convertToString((JsonElement)jsonElement, (String)"sound"));
                    list.add(new Sound(identifier, (SampledFloat)DEFAULT_FLOAT, (SampledFloat)DEFAULT_FLOAT, 1, Sound.Type.FILE, false, false, 16));
                    continue;
                }
                list.add(this.getSound(GsonHelper.convertToJsonObject((JsonElement)jsonElement, (String)"sound")));
            }
        }
        return list;
    }

    private Sound getSound(JsonObject jsonObject) {
        Identifier identifier = Identifier.parse((String)GsonHelper.getAsString((JsonObject)jsonObject, (String)"name"));
        Sound.Type type = this.getType(jsonObject, Sound.Type.FILE);
        float f = GsonHelper.getAsFloat((JsonObject)jsonObject, (String)"volume", (float)1.0f);
        Validate.isTrue((f > 0.0f ? 1 : 0) != 0, (String)"Invalid volume", (Object[])new Object[0]);
        float g = GsonHelper.getAsFloat((JsonObject)jsonObject, (String)"pitch", (float)1.0f);
        Validate.isTrue((g > 0.0f ? 1 : 0) != 0, (String)"Invalid pitch", (Object[])new Object[0]);
        int i = GsonHelper.getAsInt((JsonObject)jsonObject, (String)"weight", (int)1);
        Validate.isTrue((i > 0 ? 1 : 0) != 0, (String)"Invalid weight", (Object[])new Object[0]);
        boolean bl = GsonHelper.getAsBoolean((JsonObject)jsonObject, (String)"preload", (boolean)false);
        boolean bl2 = GsonHelper.getAsBoolean((JsonObject)jsonObject, (String)"stream", (boolean)false);
        int j = GsonHelper.getAsInt((JsonObject)jsonObject, (String)"attenuation_distance", (int)16);
        return new Sound(identifier, (SampledFloat)ConstantFloat.of((float)f), (SampledFloat)ConstantFloat.of((float)g), i, type, bl2, bl, j);
    }

    private Sound.Type getType(JsonObject jsonObject, Sound.Type type) {
        Sound.Type type2 = type;
        if (jsonObject.has("type")) {
            type2 = Sound.Type.getByName(GsonHelper.getAsString((JsonObject)jsonObject, (String)"type"));
            Objects.requireNonNull(type2, "Invalid type");
        }
        return type2;
    }

    public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return this.deserialize(jsonElement, type, jsonDeserializationContext);
    }
}

