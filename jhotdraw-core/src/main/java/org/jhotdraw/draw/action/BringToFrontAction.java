/*
 * @(#)BringToFrontAction.java
 *
 * Copyright (c) 2003-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.figure.Figure;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * ToFrontAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BringToFrontAction extends AbstractSelectedAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.bringToFront";

    /**
     * Creates a new instance.
     */
    public BringToFrontAction(DrawingEditor editor) {
        super(editor);
        configureAction();
        updateEnabledState();
    }

    /**
     * Configures the action using ResourceBundleUtil.
     */
    private void configureAction() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        final DrawingView view = getView();
        if (view != null) {
            final List<Figure> figures = new LinkedList<>(view.getSelectedFigures());
            executeBringToFront(view, figures); // Refactored logic
        }
    }

    /**
     * Handles the bring-to-front operation and undoable edit.
     */
    private void executeBringToFront(DrawingView view, List<Figure> figures) {
        bringToFront(view, figures);
        fireUndoableEditHappened(createUndoableEdit(view, figures));
    }

    private UndoableEdit createUndoableEdit(DrawingView view, List<Figure> figures) {
        return new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                return getActionName();
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                BringToFrontAction.bringToFront(view, figures);
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                SendToBackAction.sendToBack(view, figures);
            }
        };
    }

    /**
     * Retrieves the action's name from the resource bundle.
     */
    private String getActionName() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        return labels.getTextProperty(ID);
    }

    public static void bringToFront(DrawingView view, Collection<Figure> figures) {
        Drawing drawing = view.getDrawing();
        if (drawing != null) {
            figures.stream()
                    .sorted(Comparator.comparingInt(drawing::indexOf))
                    .forEach(drawing::bringToFront);
        }
    }
}