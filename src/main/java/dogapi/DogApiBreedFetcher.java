package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API. Note that all failures get reported
 * as BreedNotFoundException exceptions to align with the requirements of the BreedFetcher
 * interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /*
     * Fetch the JSON object from the given URL, if possible.
     *
     * @param url the url for the API call
     * 
     * @return JSONObject given as the response from the API call
     * 
     * @throws IOException if the API call fails
     */

    JSONObject run(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            return new JSONObject(result);

        }

    }


    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * 
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any
     *         reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        try {
            String url = "https://dog.ceo/api/breed/" + breed + "/list";
            JSONObject obj = run(url);

            if (!obj.getString("status").equals("success")) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray subBreedsArray = obj.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>();
            for (int i = 0; i < subBreedsArray.length(); i++) {
                subBreeds.add(subBreedsArray.getString(i));
            }

            return subBreeds;

        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}
