import javax.swing.*;
import java.awt.Font;

public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            configureAppearance();
            new RentalApp().setVisible(true);
        });
    }

    static void configureAppearance() {
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("CheckBox.font", new Font("SansSerif", Font.PLAIN, 12));
        UIManager.put("ComboBox.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 16));
    }
}
