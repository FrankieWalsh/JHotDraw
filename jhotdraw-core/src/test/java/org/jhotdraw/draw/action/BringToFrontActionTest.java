package org.jhotdraw.draw.action;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


import javax.swing.undo.UndoableEdit;
import java.util.*;

class BringToFrontActionTest {

    private BringToFrontAction action;
    private DrawingEditor editor;
    private DrawingView view;
    private Drawing drawing;
    private Figure figure1, figure2, figure3;

    @BeforeEach
    void setUp() {
        editor = mock(DrawingEditor.class);
        view = mock(DrawingView.class);
        drawing = mock(Drawing.class);
        figure1 = mock(Figure.class);
        figure2 = mock(Figure.class);
        figure3 = mock(Figure.class);

        List<Figure> figureList = new ArrayList<>(Arrays.asList(figure1, figure2, figure3));

        when(drawing.indexOf(any(Figure.class))).thenAnswer(invocation -> {
            Figure figure = invocation.getArgument(0);
            return figureList.indexOf(figure);
        });

        doAnswer(invocation -> {
            Figure figure = invocation.getArgument(0);
            figureList.remove(figure);
            figureList.add(figure);
            return null;
        }).when(drawing).bringToFront(any(Figure.class));

        doAnswer(invocation -> {
            Figure figure = invocation.getArgument(0);
            figureList.remove(figure);
            figureList.add(0, figure);
            return null;
        }).when(drawing).sendToBack(any(Figure.class));

        when(editor.getActiveView()).thenReturn(view);
        when(view.getDrawing()).thenReturn(drawing);
        when(view.getSelectedFigures()).thenReturn(new HashSet<>(Arrays.asList(figure1, figure2, figure3)));

        action = new BringToFrontAction(editor);
    }

    @Test
    void testBringToFrontOrder() {
        BringToFrontAction.bringToFront(view, Arrays.asList(figure1, figure2, figure3));

        verify(drawing).bringToFront(figure3);
        verify(drawing).bringToFront(figure2);
        verify(drawing).bringToFront(figure1);
    }

    @Test
    void testActionPerformedExecutesBringToFront() {
        action.actionPerformed(null);

        verify(view).getSelectedFigures();
        verify(drawing).bringToFront(figure1);
        verify(drawing).bringToFront(figure2);
        verify(drawing).bringToFront(figure3);
    }

    @Test
    void testUndoableEditUndo() {
        ArgumentCaptor<UndoableEdit> editCaptor = ArgumentCaptor.forClass(UndoableEdit.class);

        action.actionPerformed(null);

        verify(drawing).fireUndoableEditHappened(editCaptor.capture());
        UndoableEdit edit = editCaptor.getValue();
        assertNotNull(edit, "UndoableEdit should not be null");

        edit.undo();

        verify(drawing, atLeastOnce()).sendToBack(figure1);
        verify(drawing, atLeastOnce()).sendToBack(figure2);
        verify(drawing, atLeastOnce()).sendToBack(figure3);
    }

    @Test
    void testUndoableEditRedo() {
        ArgumentCaptor<UndoableEdit> editCaptor = ArgumentCaptor.forClass(UndoableEdit.class);

        action.actionPerformed(null);

        verify(drawing).fireUndoableEditHappened(editCaptor.capture());
        UndoableEdit edit = editCaptor.getValue();
        assertNotNull(edit, "UndoableEdit should not be null");

        edit.undo();

        edit.redo();

        verify(drawing, atLeastOnce()).bringToFront(figure1);
        verify(drawing, atLeastOnce()).bringToFront(figure2);
        verify(drawing, atLeastOnce()).bringToFront(figure3);
    }

    @Test
    void testNoActionWhenNoFiguresSelected() {
        when(view.getSelectedFigures()).thenReturn(Collections.emptySet());

        action.actionPerformed(null);

        verify(drawing, never()).bringToFront(any(Figure.class));
    }
    // BDD Tests
    @Test
    void givenSelectedFigures_WhenBringToFrontActionIsPerformed_ThenFiguresAreReordered() {
        // GIVEN
        Set<Figure> selectedFigures = view.getSelectedFigures();
        assertNotNull(selectedFigures, "Selected figures should not be null");
        assertTrue(selectedFigures.contains(figure1), "Selected figures should contain figure1");
        assertTrue(selectedFigures.contains(figure2), "Selected figures should contain figure2");
        assertTrue(selectedFigures.contains(figure3), "Selected figures should contain figure3");

        // WHEN
        action.actionPerformed(null);

        // THEN
        verify(drawing).bringToFront(figure3);
        verify(drawing).bringToFront(figure2);
        verify(drawing).bringToFront(figure1);
    }

    @Test
    void givenNoSelectedFigures_WhenBringToFrontActionIsPerformed_ThenNoChangesOccur() {
        // GIVEN
        when(view.getSelectedFigures()).thenReturn(Collections.emptySet());

        // WHEN
        action.actionPerformed(null);

        // THEN
        verify(drawing, never()).bringToFront(any(Figure.class));
        Set<Figure> selectedFigures = view.getSelectedFigures();
        assertTrue(selectedFigures.isEmpty(), "Selected figures should be empty");
    }

    @Test
    void givenUndoableEdit_WhenUndoIsPerformed_ThenFiguresAreSentToBack() {
        // GIVEN
        ArgumentCaptor<UndoableEdit> editCaptor = ArgumentCaptor.forClass(UndoableEdit.class);

        action.actionPerformed(null);

        verify(drawing).fireUndoableEditHappened(editCaptor.capture());
        UndoableEdit edit = editCaptor.getValue();
        assertNotNull(edit, "UndoableEdit should not be null");

        // WHEN
        edit.undo();

        // THEN
        verify(drawing, atLeastOnce()).sendToBack(figure1);
        verify(drawing, atLeastOnce()).sendToBack(figure2);
        verify(drawing, atLeastOnce()).sendToBack(figure3);
    }

    @Test
    void givenUndoableEdit_WhenRedoIsPerformed_ThenFiguresAreBroughtToFront() {
        // GIVEN
        ArgumentCaptor<UndoableEdit> editCaptor = ArgumentCaptor.forClass(UndoableEdit.class);

        action.actionPerformed(null);

        verify(drawing).fireUndoableEditHappened(editCaptor.capture());
        UndoableEdit edit = editCaptor.getValue();
        assertNotNull(edit, "UndoableEdit should not be null");

        edit.undo();

        // WHEN
        edit.redo();

        // THEN
        verify(drawing, atLeastOnce()).bringToFront(figure1);
        verify(drawing, atLeastOnce()).bringToFront(figure2);
        verify(drawing, atLeastOnce()).bringToFront(figure3);
    }
}

