package com.nloops.myrecipe.utils;

import android.util.Log;

import com.nloops.myrecipe.data.IngredientsModel;
import com.nloops.myrecipe.data.RecipeModel;
import com.nloops.myrecipe.data.StepsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will help to handle network operations and parse JSON response
 * and return the required data
 */

public class NetworkUtils {

    // Tag to help me with logs.
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // Base Json URL which include all recipes data
    public static final String BASE_JSON_RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    // set the requested method from the Server
    private static final String CONNECTION_METHOD = "GET";
    // set Connect Timeout in milliseconds 1000 = 1 sec
    private static final int CONNECT_TIMEOUT = 10000;
    // set Read Timeout in milliseconds 1000 = 1 sec
    private static final int READ_TIMEOUT = 15000;
    // server code that connection ok and we can perform our actions
    private static final int CODE_OK = 200;

    // Vars For JSON Array & Objects Names
    private static final String JSON_RECIPE_ID = "id";
    private static final String JSON_RECIPE_NAME = "name";
    private static final String JSON_ARRAY_INGREDIENTS = "ingredients";
    private static final String JSON_INGREDIENTS_QUANTITY = "quantity";
    private static final String JSON_INGREDIENTS_MEASURE = "measure";
    private static final String JSON_INGREDIENTS_INGREDIENT = "ingredient";
    private static final String JSON_ARRAY_STEPS = "steps";
    private static final String JSON_STEPS_ID = "id";
    private static final String JSON_STEPS_S_DESCRIPTION = "shortDescription";
    private static final String JSON_STEPS_DESCRIPTION = "description";
    private static final String JSON_STEPS_VIDEO_URL = "videoURL";
    private static final String JSON_STEPS_THUMBNAIL_URL = "thumbnailURL";
    private static final String JSON_RECIPE_SERVINGS = "servings";
    private static final String JSON_RECIPE_IMAGE = "image";


    /**
     * Helper Method that merge all Network Methods work into one place.
     *
     * @param urlstring
     */
    public static ArrayList<RecipeModel> fireUpNetwork(String urlstring) {
        if (urlstring != null && urlstring.length() > 0) {
            String serverResponse = makeHttpConnection(createUrlFromString(urlstring));
            return parseJSONdata(serverResponse);
        }
        return null;


    }

    /**
     * Create URL object from passing urlString
     *
     * @param urlString
     * @return URL
     */
    private static URL createUrlFromString(String urlString) {
        if (urlString == null || urlString.equals("")) return null;
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "createUrlFromString: cannot convert string into URL object \n ", e);
        }

        return url;
    }

    /**
     * Create Connection with server and get the data into input Stream and
     * return the data into String.
     *
     * @param url
     * @return String with Server Response
     */
    private static String makeHttpConnection(URL url) {
        if (url == null) return null;
        String serverResponse = "";

        InputStream inputStream = null;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(CONNECTION_METHOD);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();
            if (connection.getResponseCode() == CODE_OK) {

                inputStream = connection.getInputStream();
                serverResponse = readFromServer(inputStream);
            } else {
                Log.i(TAG, "makeHttpConnection: UnSuccessful Connection to server");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "makeHttpConnection: cannot read data from server \n ", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return serverResponse;

    }

    /**
     * Helper Method that translate the Server Stream into Readable String
     *
     * @param inputStream
     * @return String ServerResponse
     * we can use this response into JSON parsing.
     */
    private static String readFromServer(InputStream inputStream) {
        if (inputStream == null) return null;
        StringBuilder builder = new StringBuilder();
        String serverResponse;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverResponse = builder.toString();
        return serverResponse;

    }


    /**
     * Helper Method take the Server Response and return arranged data into Model.
     *
     * @param serverRespone
     * @return Model Contains Each Recipe Data.
     */
    private static ArrayList<RecipeModel> parseJSONdata(String serverRespone) {
        ArrayList<RecipeModel> recipes = new ArrayList<>();
        try {
            JSONArray rootArray = new JSONArray(serverRespone);
            for (int i = 0; i < rootArray.length(); i++) {
                // get the current object into the array loop
                JSONObject currentObject = rootArray.optJSONObject(i);
                int recipeID = currentObject.optInt(JSON_RECIPE_ID);
                String recipeName = currentObject.optString(JSON_RECIPE_NAME);

                List<IngredientsModel> ingredients = new ArrayList<>();

                // check if this Object has the ingredients Array
                // get  data into igredients Array
                if (currentObject.has(JSON_ARRAY_INGREDIENTS)) {
                    JSONArray ingredientsArray = currentObject.getJSONArray(JSON_ARRAY_INGREDIENTS);
                    for (int x = 0; x < ingredientsArray.length(); x++) {
                        JSONObject iCurrentObject = ingredientsArray.optJSONObject(x);
                        double quantity = iCurrentObject.optDouble(JSON_INGREDIENTS_QUANTITY);
                        String measure = iCurrentObject.optString(JSON_INGREDIENTS_MEASURE);
                        String ingredient = iCurrentObject.optString(JSON_INGREDIENTS_INGREDIENT);
                        IngredientsModel ingredientsModel = new IngredientsModel(quantity, measure, ingredient);
                        ingredients.add(ingredientsModel);
                    }
                }

                List<StepsModel> steps = new ArrayList<>();

                //check if this object has the Steps Array.
                // get data into Steps array.
                if (currentObject.has(JSON_ARRAY_STEPS)) {
                    JSONArray stepsArray = currentObject.getJSONArray(JSON_ARRAY_STEPS);
                    for (int y = 0; y < stepsArray.length(); y++) {
                        JSONObject sCurrentObject = stepsArray.optJSONObject(y);
                        int stepID = sCurrentObject.optInt(JSON_STEPS_ID);
                        String stepSDescription = sCurrentObject.optString(JSON_STEPS_S_DESCRIPTION);
                        String stepDescription = sCurrentObject.optString(JSON_STEPS_DESCRIPTION);
                        String stepVideoURL = sCurrentObject.optString(JSON_STEPS_VIDEO_URL);
                        String stepThumbnailURL = sCurrentObject.optString(JSON_STEPS_THUMBNAIL_URL);

                        StepsModel stepsModel = new StepsModel(stepID, stepSDescription,
                                stepDescription, stepVideoURL, stepThumbnailURL);

                        steps.add(stepsModel);

                    }
                }

                int servings = currentObject.optInt(JSON_RECIPE_SERVINGS);
                String image = currentObject.optString(JSON_RECIPE_IMAGE);

                RecipeModel recipeModel = new RecipeModel(
                        recipeID,
                        recipeName,
                        ingredients,
                        steps,
                        servings,
                        image
                );

                recipes.add(recipeModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipes;
    }


}
