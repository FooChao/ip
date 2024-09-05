package sumode;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sumode.exception.SumoDException;
import sumode.ui.Ui;
import sumode.util.Command;
import sumode.util.Parser;
import sumode.util.Storage;
import sumode.util.SumoTaskList;


/**
 * Main class where user will run the Main from
 */
public class SumoDE extends Application {

    private Storage storage = null;
    private SumoTaskList tasks;
    private Ui ui;

    /**
     * Constructor for SumoDE
     * @param filePath FilePath to save data.
     */
    public SumoDE(String filePath, Ui ui) {
        //handle Ui
        this.ui = ui;

        // handle Storage
        try {
            this.storage = new Storage(filePath);
        } catch (IOException e) {
            // Note: this will only happen when file don't exist and we cannot create new file in the path.
            // New File will be created when file doesn't exist in first place.
            ui.unknownSaveError();
        }

        //handle SumoTaskList
        if (this.storage == null) {
            this.tasks = new SumoTaskList(this.ui); // we will use the version where we cannot save
        } else {
            try {
                this.tasks = new SumoTaskList(this.storage, this.ui);
            } catch (IOException e) {
                //unlikely will happen since we already successfully initialise storage
                ui.unknownSaveError();
                this.tasks = new SumoTaskList(this.ui); // we will use the version where we cannot save
            }
        }
    }

    public SumoDE() {
        // this is just so that launcher can launch this
    }

    /**
     * Runs a task-management chatbot SumoDE
     */
    public void execute(String input) {
        boolean canTerminate = false;
        // Splitting command and action
        String[] splitString = Parser.splitCommandAndAction(input);
        String commandString = splitString[0];
        String item = splitString[1];
        Command command;

        // find correct matching command
        try {
            command = Command.valueOf(commandString.toUpperCase());
            canTerminate = this.tasks.execute(command, item);
        } catch (IllegalArgumentException e) {
            ui.unknownCommand(commandString);
        } catch (SumoDException e) {
            ui.handleError(e);
        } finally {
            if (!canTerminate) {
                ui.next();
            } else {
                ui.bye();
                Platform.exit();
            }
        }
    }


    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SumoDE.class.getResource("/view/Ui.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<Ui>getController().setSumoDE(); // inject the Duke instance
            stage.setTitle("SumoDE");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
