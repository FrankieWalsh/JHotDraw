import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.text.FloatingTextField;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class FloatingTextFieldTest {

    private FloatingTextField floatingTextField;
    private TextHolderFigure mockFigure;
    private DrawingView mockView;
    private JComponent mockComponent;
    private Graphics mockGraphics;

    @Before
    public void setUp() {
        floatingTextField = new FloatingTextField();
        mockFigure = mock(TextHolderFigure.class);
        mockView = mock(DrawingView.class);

        // Mock the JComponent instead of using a real JPanel
        mockComponent = mock(JComponent.class);
        when(mockView.getComponent()).thenReturn(mockComponent);

        // Mock the font to prevent NullPointerException in updateWidget
        Font mockFont = new Font("Arial", Font.PLAIN, 12);
        when(mockFigure.getFont()).thenReturn(mockFont);
        when(mockFigure.getFontSize()).thenReturn(12.0F);
        when(mockFigure.getTextColor()).thenReturn(Color.BLACK);
        when(mockFigure.getFillColor()).thenReturn(Color.WHITE);

        // Mock the bounds to prevent NullPointerException in updateWidget
        Rectangle2D.Double mockBounds = new Rectangle2D.Double(0, 0, 100, 50);
        when(mockFigure.getBounds()).thenReturn(mockBounds);

        // Mock the drawingToView conversion to return a valid Point and Rectangle
        when(mockView.drawingToView(any(Point2D.Double.class))).thenReturn(new Point(10, 10));
        when(mockView.drawingToView(any(Rectangle2D.Double.class))).thenReturn(new Rectangle(10, 10, 100, 50));

        // Mock Graphics for the JTextField
        mockGraphics = mock(Graphics.class);
        FontMetrics mockFontMetrics = mock(FontMetrics.class);
        when(mockGraphics.getFontMetrics(any(Font.class))).thenReturn(mockFontMetrics);

        // Inject the mockGraphics into FloatingTextField for testing
        floatingTextField.setTestGraphics(mockGraphics);
    }

    @Test
    public void testCreateOverlaySetsUpCorrectly() {
        when(mockFigure.getText()).thenReturn("Test Text");
        when(mockFigure.getTextColumns()).thenReturn(10);
        floatingTextField.createOverlay(mockView, mockFigure);
        assertEquals("Test Text", floatingTextField.getText());
        verify(mockView).getComponent();
    }

    @Test
    public void testEndOverlayCleansUp() {
        // Arrange
        floatingTextField.createOverlay(mockView, mockFigure);

        // Act
        floatingTextField.endOverlay();

        // Assert
        assertFalse(floatingTextField.getTextField().isVisible());  // Verify that textField is not visible
        verify(mockComponent).remove(floatingTextField.getTextField());  // Verify that textField was removed from the component
    }

    @Test
    public void testOverlayHandlesEmptyText() {
        // Arrange
        when(mockFigure.getText()).thenReturn("");

        // Act
        floatingTextField.createOverlay(mockView, mockFigure);

        // Assert
        assertEquals("", floatingTextField.getText());
    }

    @Test
    public void testOverlayHandlesExtremeFontSizes() {
        // Arrange
        when(mockFigure.getFontSize()).thenReturn(100.0F);
        when(mockFigure.getText()).thenReturn("Large Font Text");

        // Act
        floatingTextField.createOverlay(mockView, mockFigure);

        // Assert
        Dimension preferredSize = floatingTextField.getPreferredSize(10);
        assertNotNull(preferredSize);
        assertTrue(preferredSize.width > 0 && preferredSize.height > 0);
    }
}
