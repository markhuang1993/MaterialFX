/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgressSpinnersDemoController implements Initializable {

    @FXML
    private MFXProgressSpinner progress1;

    @FXML
    private MFXProgressSpinner progress2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnimationUtils.TimelineBuilder.build()
                .add(
                        KeyFrames.of(
                                Duration.ZERO,
                                new KeyValue(progress1.progressProperty(), 0),
                                new KeyValue(progress2.progressProperty(), 0)
                        ),
                        KeyFrames.of(
                                Duration.seconds(0.5),
                                new KeyValue(progress1.progressProperty(), 0.5)
                        ),
                        KeyFrames.of(
                                Duration.seconds(2),
                                new KeyValue(progress1.progressProperty(), 1),
                                new KeyValue(progress2.progressProperty(), 1)
                        )
                )
                .setCycleCount(Timeline.INDEFINITE)
                .getAnimation()
                .play();

        progress1.getRanges1().add(NumberRange.of(0.0, 0.30));
        progress1.getRanges2().add(NumberRange.of(0.31, 0.60));
        progress1.getRanges3().add(NumberRange.of(0.61, 1.0));
    }
}
