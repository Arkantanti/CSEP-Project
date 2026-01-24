package client.utils;

import client.scenes.MainCtrl;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;

/**
 * Service that repeatedly polls favoritesManager and
 * checks with the server if they still exist
 * through the validate method in the FavoritesManager class.
 * If favorites are deleted by another user, the polling service will
 * take notice of this and notify the user trough an alert.
 */
public class FavoritesPollingService {

    private FavoritesManager favoritesManager;
    private MainCtrl mainCtrl;
    private Thread pollingThread;
    private boolean running = false;
    // the interval between polls
    private static final int POLLING_INTERVAL_SECONDS = 10;

    /**
     * Constructs a new FavoritesPollingService.
     *
     * @param favoritesManager the manager for handling favorited recipes
     */
    @Inject
    public FavoritesPollingService(FavoritesManager favoritesManager) {
        this.favoritesManager = favoritesManager;
    }

    /**
     * Sets the MainCtrl controller so the thread can refresh UI.
     * This must be called before starting the
     * polling service, otherwise a NullPointerException will occur.
     *
     * @param mainCtrl the main controller
     */
    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Starts the polling thread.
     * The thread checks every POLLING_INERVAL_SECONDS
     * seconds if favorited recipes still exist on the server.
     */
    public void startPollingService() {
        if (running) {
            return;
        }

        running = true;
        pollingThread = new Thread(() -> pollLoop());
        // set the tread daemon true so it doesn't keep the JVM alive.
        pollingThread.setDaemon(true);
        pollingThread.start();
        System.out.println("The favorites polling service thread has started with an interval of "
                + POLLING_INTERVAL_SECONDS + " seconds per interval.");
    }

    /**
     * The main polling loop that the thread runs.
     * Periodicaly validates favorites and alerts the user if there were deleted recipes.
     */
    private void pollLoop() {
        while (running) {
            try {
                // keep the thread inactive for the interval.
                Thread.sleep(POLLING_INTERVAL_SECONDS * 1000);

                // fetch the list of removed recipe IDs
                List<Long> removedIds = favoritesManager.validate();

                //  if there are deleted recipes, show the alert to the user.
                if (!removedIds.isEmpty()) {
                    showDeletedAlert(removedIds);
                }

            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.err.println("Error validating " +
                        "favorite recipes in the config file: " + e.getMessage());
            }
        }
    }

    /**
     * Shows an alert to indicate to the user that some favorited recipes were deleted.
     *
     * @param removedIds the list of recipe IDs that were deleted
     */
    private void showDeletedAlert(List<Long> removedIds) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Favorites Deleted");
            alert.setHeaderText("Recipe(s) deleted by another user");
            alert.setContentText(
                removedIds.size() + " favorite recipe(s) were deleted from the server.\n" +
                "They have been removed from your favorites."
            );
            alert.show();

            mainCtrl.reloadRecipes();
        });
    }

    /**
     * Stops the polling service and shuts down the running thread.
     */
    public void shutdown() {
        running = false;
        if (pollingThread != null) {
            pollingThread.interrupt();
        }
    }
}

