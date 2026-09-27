package br.com.cactuserp.pdv.config.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private DateTimeUtil() {}

    public static void dateTimeNow(Label label) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    LocalDateTime agora = LocalDateTime.now();
                    label.setText("Data e Hora: ".concat(agora.format(format)));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Integer.MAX_VALUE);
        timeline.play();
    }
}
