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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.enums.StepperToggleState;
import io.github.palexdev.materialfx.controls.enums.TextPosition;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.DialogUtils;
import io.github.palexdev.materialfx.utils.LabelUtils;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import io.github.palexdev.materialfx.validation.MFXPriorityValidator;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXStepperToggle}.
 * <p>
 * It consists of a {@link Circle} with the css id set to "circle", a {@link MFXLabel}
 * and a little optional error icon shown in the upper right corner of the toggle.
 */
public class MFXStepperToggleSkin extends SkinBase<MFXStepperToggle> {
    private final StackPane container;
    private final Circle circle;
    private final MFXLabel label;
    private final MFXIconWrapper errorIcon;

    public MFXStepperToggleSkin(MFXStepperToggle stepperToggle) {
        super(stepperToggle);

        circle = new Circle(0, Color.LIGHTGRAY);
        circle.setId("circle");
        circle.radiusProperty().bind(stepperToggle.sizeProperty());
        circle.strokeWidthProperty().bind(stepperToggle.strokeWidthProperty());
        circle.setStrokeType(StrokeType.CENTERED);

        container = new StackPane(circle, stepperToggle.getIcon());
        container.getStylesheets().setAll(stepperToggle.getUserAgentStylesheet());

        label = new MFXLabel();
        label.getStylesheets().addAll(stepperToggle.getUserAgentStylesheet());
        label.setText(stepperToggle.getText());
        label.setLineColor(Color.TRANSPARENT);
        label.setUnfocusedLineColor(Color.TRANSPARENT);
        label.setManaged(false);

        errorIcon = new MFXIconWrapper(
                new MFXFontIcon("mfx-exclamation-triangle", Color.web("#EF6E6B")), 16
        );
        errorIcon.setId("errorIcon");
        errorIcon.setVisible(false);
        errorIcon.setManaged(false);

        getChildren().addAll(container, label, errorIcon);
        setListeners();
    }

    /**
     * Adds the following listeners, handlers/filters.
     * <p>
     * <p> - Adds a listener to the toggle's state property to show the error icon when
     * the state is ERROR and if the {@link MFXStepperToggle#showErrorIconProperty()} is true.
     * <p> - Adds a listener to the {@link MFXStepperToggle#showErrorIconProperty()} to make the error icon
     * work properly, if the property value changes and the state is ERROR or not.
     * <p> - Adds a listener to the {@link MFXStepperToggle#textPositionProperty()} to re-compute the layout when it changes.
     * <p> - Adds a listener to the validator's {@link AbstractMFXValidator#validProperty()} to properly update the
     * toggle's state.
     * <p> - Adds an handler for MOUSE_PRESSED events to the error icon to call {@link #showErrorsDialog()}.
     */
    private void setListeners() {
        MFXStepperToggle stepperToggle = getSkinnable();
        MFXDialogValidator validator = stepperToggle.getValidator();

        stepperToggle.stateProperty().addListener((observable, oldValue, newValue) ->
                errorIcon.setVisible(newValue == StepperToggleState.ERROR && stepperToggle.isShowErrorIcon()));

        stepperToggle.showErrorIconProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorIcon.setVisible(stepperToggle.getState() == StepperToggleState.ERROR);
            } else {
                errorIcon.setVisible(false);
            }
        });

        stepperToggle.labelTextGapProperty().addListener(invalidated -> stepperToggle.requestLayout());
        stepperToggle.textPositionProperty().addListener(invalidated -> stepperToggle.requestLayout());

        validator.validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && stepperToggle.getState() == StepperToggleState.ERROR) {
                stepperToggle.setState(StepperToggleState.SELECTED);
            }
        });

        errorIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> showErrorsDialog());
        label.visibleProperty().bind(stepperToggle.textProperty().isEmpty().not());
    }

    /**
     * Shows an error dialog that contains the all the validator's unmet conditions, including
     * the dependency ones too.
     * <p>
     * Uses {@link DialogUtils} to build and show the dialog.
     */
    protected void showErrorsDialog() {
        MFXStepperToggle stepperToggle = getSkinnable();
        MFXDialogValidator validator = stepperToggle.getValidator();
        StringBuilder sb = new StringBuilder(validator.getUnmetMessages());
        validator.getDependencies().stream()
                .filter(v -> v instanceof MFXPriorityValidator)
                .map(v -> (MFXPriorityValidator) v)
                .forEach(v -> sb.append(v.getUnmetMessages()));
        MFXStageDialog dialog = DialogUtils.getStageDialog(stepperToggle.getScene().getWindow(), DialogType.ERROR, validator.getTitle(), sb.toString());
        dialog.setScrimBackground(true);
        dialog.show();
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + container.prefWidth(-1) + (getSkinnable().getLabelTextGap() * 2) + (label.getHeight() * 2) + bottomInset;
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + Math.max(circle.getRadius() * 2, label.getWidth()) + rightInset;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        MFXStepperToggle stepperToggle = getSkinnable();

        double lw = snapSizeX(LabelUtils.computeMFXLabelWidth(label));
        double lh = snapSizeY(LabelUtils.computeTextHeight(label.getFont(), label.getText()));
        double lx = snapPositionX(circle.getBoundsInParent().getCenterX() - (lw / 2.0));
        double ly = 0;

        if (stepperToggle.getTextPosition() == TextPosition.BOTTOM) {
            label.setTranslateY(0);
            ly = snapPositionY(circle.getBoundsInParent().getMaxY() + stepperToggle.getLabelTextGap());
            label.resizeRelocate(lx, ly, lw, lh);
        } else {
            label.resizeRelocate(lx, ly, lw, lh);
            label.setTranslateY(-stepperToggle.getLabelTextGap() - lh);
        }

        double ix = snapPositionX(circle.getBoundsInParent().getMaxX());
        double iy = snapPositionY(circle.getBoundsInParent().getMinY() - 6);
        errorIcon.resizeRelocate(ix, iy, 16, 16);
    }
}
