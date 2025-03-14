package ro.mta.sdk.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;

public class JsonToggleParser {
    public static String toJsonString(ToggleCollection toggleCollection) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(toggleCollection);
    }

    public static ToggleCollection fromJson(Reader reader) throws IllegalStateException {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(
                                ToggleCollection.class, new JsonToggleCollectionDeserializer())
                        .create();
        ToggleCollection gsonCollection = gson.fromJson(reader, ToggleCollection.class);
        if (gsonCollection == null) {
            throw new IllegalStateException("Could not extract toggles from json");
        }
        return new ToggleCollection(gsonCollection.getFeatureToggles());
    }
}
