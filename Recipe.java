import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Sort of a helper class for the main class
 */
public class Recipe {

    private JSONObject wegmansRecipe;

    private JSONArray wegmansIngredients;

    private String recipeName;

    private boolean usingBarcodes = false;

    /** recipe unique id*/
    private int id;
    /** link of the recipe*/
    private String link;

    private ArrayList<String> additionalIngredients = null;

    private ArrayList<Long> barcodes = null;

    private ArrayList<String> productName;

    private HashMap<Long, Ingredient> ingredients;

    public Recipe(int id, boolean usingBarcodes, String recipeName) throws IOException, JSONException{
        this.usingBarcodes = usingBarcodes;
        this.id = id;
        this.link = "https://api.wegmans.io/meals/recipes/";
        this.recipeName = recipeName;


        barcodes = new ArrayList<>();
        productName = new ArrayList<>();
        ingredients = new HashMap<>();


        //setRecipeName();
    }

    public String getRecipeName() {
        return recipeName.toUpperCase();
    }

    /**
     *  Get the recipe id.
     *
     * @return recipe id
     */
    public int getId() {return this.id;}

    private String getLink() {
        return this.link +
                this.id +
                "/?api-version=2018-10-18&Subscription-Key=de5a8b899d5f47049489b07659888146";
    }

    private void setRecipeName() throws JSONException, IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getLink())
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "f14a725e-40ef-4b72-bc01-10f36bba0278")
                .build();

        Response response = client.newCall(request).execute();
        String responseInString = response.body().string();

        wegmansRecipe = new JSONObject(responseInString);
        wegmansIngredients = wegmansRecipe.getJSONArray("ingredients");

        recipeName = wegmansRecipe.getString("name");
    }

    public void getIngredients() throws IOException, JSONException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getLink())
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "f14a725e-40ef-4b72-bc01-10f36bba0278")
                .build();

        Response response = client.newCall(request).execute();
        String responseInString = response.body().string();

        JSONObject wegmansRecipe = new JSONObject(responseInString);
        JSONArray wegmansIngredients = wegmansRecipe.getJSONArray("ingredients");
        getBarcodes(wegmansIngredients);

        /*
        if(usingBarcodes)
            getBarcodes(wegmansIngredients);
        else
            getName(wegmansRecipe, wegmansIngredients);
            */
    }

    private void getBarcodes(JSONArray ingredients) throws JSONException, IOException{
        ArrayList<JSONArray> linksArray = new ArrayList<>();
        JSONArray templinks;
        OkHttpClient client2;
        Request request2;
        Response response2;
        String responseInString2;
        for(int i = 0; i < ingredients.length(); i++){
            try{
                templinks = (ingredients.getJSONObject(i)).getJSONArray("_links");
                //linksArray.add(templinks);

                JSONObject product = ingredients.getJSONObject(i);
                String name = product.getString("name");
                productName.add(name);

                client2 = new OkHttpClient();
                request2 = new Request.Builder()
                        .url("https://api.wegmans.io/" + templinks.getJSONObject(0).getString("href")
                                + "&Subscription-Key=de5a8b899d5f47049489b07659888146")
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .addHeader("Postman-Token", "f14a725e-40ef-4b72-bc01-10f36bba0278")
                        .build();

                response2 = client2.newCall(request2).execute();
                responseInString2 = response2.body().string();

                String barcodeAsString = (new JSONObject(responseInString2)).getJSONArray("tradeIdentifiers").getJSONObject(0)
                        .getJSONArray("barcodes").getJSONObject(0).getString("barcode");
                long barcode = Long.parseLong(barcodeAsString);
                barcodes.add(barcode);

                this.ingredients.put(barcode, new Ingredient(barcode, name));
            }catch(JSONException jsonEx){
                if(jsonEx.getMessage().equals("JSONObject[\"_links\"] not found.")){
                    if(additionalIngredients == null)
                        additionalIngredients = new ArrayList<>();

                    additionalIngredients.add((ingredients.getJSONObject(i)).getString("name"));
                    ingredients.remove(i);
                }else
                    throw jsonEx;
            }
        }
/*
        OkHttpClient client2;
        Request request2;
        Response response2;
        String responseInString2;
*/
        for(JSONArray links: linksArray){
            client2 = new OkHttpClient();
            request2 = new Request.Builder()
                    .url("https://api.wegmans.io/" + links.getJSONObject(0).getString("href")
                            + "&Subscription-Key=de5a8b899d5f47049489b07659888146")
                    .get()
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "f14a725e-40ef-4b72-bc01-10f36bba0278")
                    .build();

            response2 = client2.newCall(request2).execute();
            responseInString2 = response2.body().string();

            String barcodeAsString = (new JSONObject(responseInString2)).getJSONArray("tradeIdentifiers").getJSONObject(0)
                    .getJSONArray("barcodes").getJSONObject(0).getString("barcode");
            long barcode = Long.parseLong(barcodeAsString);
            barcodes.add(barcode);
        }
    }

    public void getName(JSONObject recipe, JSONArray ingredients) throws JSONException{
        for(int i=0; i<recipe.length(); i++){
            try{
                JSONObject product = ingredients.getJSONObject(i);
                String name = product.getString("name");
                productName.add(name);
            }
            catch(JSONException jsonEx){
                if(jsonEx.getMessage().equals("JSONObject[\"sku\"] not found.")){
                    if(additionalIngredients == null){
                        additionalIngredients = new ArrayList<>();
                        additionalIngredients.add((ingredients.getJSONObject(i)).getString("name"));
                    }
                    else throw jsonEx;
                }
            }
        }
    }

    public boolean removeIngredient(long barcode){
        if(ingredients.containsKey(barcode)){
            ingredients.remove(barcode);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public String toString() {
        String recipeString = "";

        recipeString += getRecipeName() + ": \n";

        //recipeString += ingredients.toString().replace(' ', '\n');
        //recipeString += ingredients.toString();

        for(Map.Entry<Long, Ingredient> entry : ingredients.entrySet()){
            recipeString += "> "+ entry.getKey() + " " + entry.getValue() + "\n";
        }

        return recipeString;

    }
}