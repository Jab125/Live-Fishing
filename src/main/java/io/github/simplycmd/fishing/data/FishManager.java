package io.github.simplycmd.fishing.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.simplycmd.fishing.Constants;
import io.github.simplycmd.fishing.data.serialization.BasicFish;
import io.github.simplycmd.fishing.data.serialization.IdkNameFish;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FishManager implements SimpleSynchronousResourceReloadListener {
    public static FishManager manager;
    private static final Gson GSON = new GsonBuilder().create();

    public static List<IdkNameFish> tradeMap = new ArrayList<>();
    public static FishManager manager() {
        if (manager == null) manager = new FishManager();
        return manager;
    }

    @Nullable
    public FishData getFish(ItemStack itemStack)
    {
        for (var data : tradeMap) {
            for (var data2 : data.getFishDataList()) {
                if (data2.itemStack().equals(itemStack)) {
                    return data2;
                }
            }
        }
        return null;
    }

    @Nullable
    public FishData getFish(Item item)
    {
        for (var data : tradeMap) {
            for (var data2 : data.getFishDataList()) {
                if (data2.itemStack().getItem().equals(item)) {
                    return data2;
                }
            }
        }
        return null;
    }

    /**
     * @return The unique identifier of this listener.
     */
    @Override
    public Identifier getFabricId() {
        return new Identifier("fishing", "fish_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        List<IdkNameFish> entityToResourceList = new ArrayList<>();
        // Clear Caches Here

        for(Identifier id : manager.findResources("fishing", path -> path.equals("fish.json"))) {
            try(InputStream stream = manager.getResource(id).getInputStream()) {
                IdkNameFish.Builder builder = IdkNameFish.Builder.create();
                Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                JsonObject object = JsonHelper.deserialize(GSON, reader, JsonObject.class);
                builder.deserialize(object);
                System.out.println(object.toString());
                entityToResourceList.add(builder.build());
                tradeMap = ImmutableList.copyOf(entityToResourceList);
                // Consume the stream however you want, medium, rare, or well done.
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
