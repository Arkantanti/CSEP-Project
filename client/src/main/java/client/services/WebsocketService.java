package client.services;

import client.config.Config;
import client.scenes.AppViewCtrl;
import client.scenes.MainCtrl;
import client.scenes.RecipeViewCtrl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import commons.SyncEvent;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class WebsocketService implements WebSocket.Listener {
    private final ObjectMapper mapper = new ObjectMapper();

    private RecipeViewCtrl recipeViewCtrl;
    private AppViewCtrl appViewCtrl;
    private MainCtrl mainCtrl;

    /**
     * injected constructor
     * @param config the config file, used for the server parameters
     */
    @Inject
    public WebsocketService(Config config) {
        connect(config.getServerUrl());
    }

    /**
     * called by MainCtrl to setup whatever needs to be setup
     * @param appviewCtrl a reference to the appviewCtrl
     * @param mainCtrl a reference to the MainCtrl
     */
    public void initialize(AppViewCtrl appviewCtrl, MainCtrl mainCtrl) {
        this.appViewCtrl = appviewCtrl;
        this.mainCtrl = mainCtrl;
    }

    public void setRecipeViewCtrl(RecipeViewCtrl recipeViewCtrl) {
        this.recipeViewCtrl = recipeViewCtrl;
    }

    /**
     * connects to the server
     * @param serverURL the url of the server to connect to.
     */
    private void connect(String serverURL){
        URI serverURI = URI.create(serverURL);
        String fullhost = serverURI.getHost() + ":" + serverURI.getPort();
        URI uri = URI.create("ws://" + fullhost + "/ws");
        System.out.println("connecting to " + uri);
        HttpClient client = HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(uri, this)
                .exceptionally(exception -> {
                    System.out.println("Error connecting to " + uri);
                    exception.printStackTrace();
                    return null;
                });
    }

    @Override
    public CompletionStage<?> onText(
        WebSocket websocket,
        CharSequence data,
        boolean last
    ){
        StringBuilder message = new StringBuilder();
        message.append(data);
        if (!last){
            websocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        try{
            SyncEvent event = mapper.readValue(message.toString(), SyncEvent.class);


            Platform.runLater(() -> {
                switch (event) {
                    case SyncEvent.RecipeCreated recipeCreated -> {
                        appViewCtrl.refreshData();
                    }
                    case SyncEvent.RecipeDeleted recipeDeleted -> {
                        appViewCtrl.refreshData();
                        if (recipeViewCtrl != null && recipeViewCtrl.getRecipe().getId() == recipeDeleted.getRecipeId()) {
                            mainCtrl.showDefaultView();
                        }
                    }
                    case SyncEvent.RecipeContentUpdated recipeContentUpdated -> {
                        if (recipeViewCtrl != null && recipeViewCtrl.getRecipe().getId() == recipeContentUpdated.getRecipeId()) {
                            recipeViewCtrl.loadRecipe(recipeContentUpdated.getRecipe());
                        }
                        appViewCtrl.refreshData();
                    }
                    case SyncEvent.RecipeIngredientCreated ingredientCreated -> {
                        if (recipeViewCtrl != null && recipeViewCtrl.getRecipe().getId() == ingredientCreated.getRecipeId()) {
                            recipeViewCtrl.loadIngredients();
                        }
                    }
                    case SyncEvent.RecipeIngredientDeleted ingredientDeleted -> {
                        if (recipeViewCtrl != null && recipeViewCtrl.getRecipe().getId() == ingredientDeleted.getRecipeId()) {
                            recipeViewCtrl.loadIngredients();
                        }
                    }
                    case SyncEvent.RecipeIngredientUpdated ingredientUpdated -> {
                        if (recipeViewCtrl != null && recipeViewCtrl.getRecipe().getId() == ingredientUpdated.getRecipeId()) {
                            recipeViewCtrl.loadIngredients();
                        }
                    }
                    default -> {
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        websocket.request(1);
        return CompletableFuture.completedFuture(null);
    }


}
