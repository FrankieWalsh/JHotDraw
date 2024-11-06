package org.jhotdraw.draw.figure;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.text.FloatingTextField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ShapeTextAnnotationTest extends ScenarioTest<
        ShapeTextAnnotationTest.GivenShape,
        ShapeTextAnnotationTest.WhenAddingText,
        ShapeTextAnnotationTest.ThenTextIsAdded> {

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void add_text_to_shape() {
        GivenShape givenShape = given().a_shape_selected_in_the_drawing_tool();
        WhenAddingText whenAddingText = when().using(givenShape.getFloatingTextField(), givenShape.getShape(), givenShape.getMockView())
                .text_is_added_to_the_shape("Annotation Text");
        then().using(whenAddingText.getFloatingTextField(), whenAddingText.getShape())
                .the_shape_displays_text("Annotation Text")
                .and().the_text_is_editable();
    }

    // Given Step
    static class GivenShape extends Stage<GivenShape> {
        @Mock
        private TextHolderFigure shape;
        @Mock
        private DrawingView mockView;
        private FloatingTextField floatingTextField;
        private JComponent mockComponent;

        @Before
        public void setup() {
            MockitoAnnotations.openMocks(this);
        }

        public GivenShape a_shape_selected_in_the_drawing_tool() {
            // Initialize mocks and floatingTextField
            floatingTextField = new FloatingTextField();
            shape = mock(TextHolderFigure.class);
            mockView = mock(DrawingView.class);
            mockComponent = mock(JComponent.class);

            // Mock behavior for the DrawingView to return a JComponent
            doReturn(mockComponent).when(mockView).getComponent();

            // Set up the font and other properties to prevent NullPointerExceptions
            Font mockFont = new Font("Arial", Font.PLAIN, 12);
            doReturn(mockFont).when(shape).getFont();
            doReturn(12.0F).when(shape).getFontSize();
            doReturn(Color.BLACK).when(shape).getTextColor();
            doReturn(Color.WHITE).when(shape).getFillColor();

            // Mock bounds and text behavior
            Rectangle2D.Double mockBounds = new Rectangle2D.Double(0, 0, 100, 50);
            doReturn(mockBounds).when(shape).getBounds();
            doReturn("Initial Text").when(shape).getText();
            doReturn(20).when(shape).getTextColumns();
            doReturn(true).when(shape).isEditable();

            return self();
        }

        // Getters to pass dependencies to other stages
        public FloatingTextField getFloatingTextField() {
            return floatingTextField;
        }

        public TextHolderFigure getShape() {
            return shape;
        }

        public DrawingView getMockView() {
            return mockView;
        }
    }

    // When Step
    static class WhenAddingText extends Stage<WhenAddingText> {
        private FloatingTextField floatingTextField;
        private TextHolderFigure shape;
        private DrawingView mockView;

        // Method to initialize dependencies passed from GivenShape
        public WhenAddingText using(FloatingTextField floatingTextField, TextHolderFigure shape, DrawingView mockView) {
            this.floatingTextField = floatingTextField;
            this.shape = shape;
            this.mockView = mockView;
            return this;
        }

        public WhenAddingText text_is_added_to_the_shape(String text) {
            // Creating overlay to simulate text editing
            floatingTextField.createOverlay(mockView, shape);
            floatingTextField.getTextField().setText(text);
            return self();
        }

        // Getters to pass dependencies to ThenTextIsAdded
        public FloatingTextField getFloatingTextField() {
            return floatingTextField;
        }

        public TextHolderFigure getShape() {
            return shape;
        }
    }

    // Then Step
    static class ThenTextIsAdded extends Stage<ThenTextIsAdded> {
        private FloatingTextField floatingTextField;
        private TextHolderFigure shape;

        // Method to initialize dependencies passed from WhenAddingText
        public ThenTextIsAdded using(FloatingTextField floatingTextField, TextHolderFigure shape) {
            this.floatingTextField = floatingTextField;
            this.shape = shape;
            return this;
        }

        public ThenTextIsAdded the_shape_displays_text(String expectedText) {
            assertThat(floatingTextField.getText()).isEqualTo(expectedText);
            verify(shape, atLeastOnce()).getText(); // Verify the text retrieval
            return self();
        }

        public ThenTextIsAdded the_text_is_editable() {
            assertThat(shape.isEditable()).isTrue();
            return self();
        }

        public ThenTextIsAdded and() {
            return self();
        }
    }
}
