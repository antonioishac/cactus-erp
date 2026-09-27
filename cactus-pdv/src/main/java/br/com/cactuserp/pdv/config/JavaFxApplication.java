package br.com.cactuserp.pdv.config;

import br.com.cactuserp.pdv.PdvApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private StageManager stageManager;

    @Override
    public void init() {
        springContext = SpringApplication.run(PdvApplication.class);
        stageManager = springContext.getBean(StageManager.class);
    }

    @Override
    public void start(Stage primaryStage) {
        stageManager.setPrimaryStage(primaryStage);
        stageManager.showLoginScene();
        //stageManager.showPdv2Scene();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}