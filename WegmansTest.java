import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.JSONException;
//import java.util.ArrayList;
import java.util.HashMap;
//import javax.json;

public class WegmansTest {
    public static void main(String[] args) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.wegmans.io/meals/recipes?api-version=2018-10-18&Subscription-Key=1a024f8e565e4b0d8df67f89af182e27")
                    .get()
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "85af3cf9-621e-458a-ac5d-2cb696328518")
                    .build();

            Response wegmansResponse = client.newCall(request).execute();
            String responseAsString = wegmansResponse.body().string();
            JSONArray recipeJsonArray = (new JSONObject(responseAsString)).getJSONArray("recipes");

            int recipeId;
            HashMap<String, Recipe> wegmansRecipes = new HashMap<>();
            for(int i = 0; i < recipeJsonArray.length(); i++){
                recipeId = recipeJsonArray.getJSONObject(i).getInt("id");
                String recipeName = recipeJsonArray.getJSONObject(i).getString("name").toUpperCase();
                wegmansRecipes.put(recipeName, new Recipe(recipeId, false, recipeName));
            }

            System.out.println("Enter the recipe you want: ");
            File file = new File(args[0]);
            Scanner fIn = new Scanner(file);
            Scanner userIn = new Scanner(System.in);
            String userInput = userIn.nextLine();
            Recipe tempRecipe;

            if(wegmansRecipes.containsKey(userInput.toUpperCase())){
                tempRecipe = wegmansRecipes.get(userInput.toUpperCase());
                tempRecipe.getIngredients();
                //System.out.println(tempRecipe);

                System.out.println("Do you want to add any barcode you have?");
                String ans = userIn.nextLine();
                long barcode = 0;

                if(ans.equals("Yes")) {
                    System.out.println("Which barcode do you have?");
                    barcode = Long.parseLong(fIn.next());
                    while (barcode!=-1) {
                        System.out.println("Which barcode do you have?");
//                        System.out.println(fIn.nextLong());


                        barcode = userIn.nextLong();

                        tempRecipe.removeIngredient(barcode);
                    }
                    System.out.println(tempRecipe);
                }
                else {
                    System.out.println(tempRecipe);
                }
            }



        } catch(IOException ioEx) {
            System.err.println(ioEx.getMessage());
            ioEx.printStackTrace();
        } catch(Exception ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}

