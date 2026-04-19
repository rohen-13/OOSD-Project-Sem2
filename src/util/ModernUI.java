package util;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public final class ModernUI {

    public static final Color APP_BACKGROUND = new Color(244, 246, 251);
    public static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    public static final Color CARD_ALT_BACKGROUND = new Color(248, 250, 255);
    public static final Color SURFACE_BORDER = new Color(219, 225, 238);
    public static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color PRIMARY = new Color(34, 99, 238);
    public static final Color PRIMARY_DARK = new Color(30, 64, 175);
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color WARNING = new Color(245, 158, 11);

    private ModernUI() {
    }

    public static JPanel createSurfacePanel(LayoutManager layout, int radius) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(createCardBorder(radius, 16));
        return panel;
    }

    public static Border createCardBorder(int radius, int padding) {
        return new CompoundBorder(
            new RoundedBorder(radius, SURFACE_BORDER),
            BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
    }

    public static Border createInputBorder() {
        return new CompoundBorder(
            new RoundedBorder(14, SURFACE_BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    public static void styleTextField(JTextComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(CARD_ALT_BACKGROUND);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(createInputBorder());
    }

    public static void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 20));
        button.setUI(new FilledButtonUI(background, foreground));
    }

    public static JLabel createTitleLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(235, 238, 245));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(236, 242, 255));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SURFACE_BORDER));
    }

    public static JScrollPane wrapScroll(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(new CompoundBorder(
            new RoundedBorder(22, SURFACE_BORDER),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        scrollPane.setBackground(CARD_BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    public static void styleTabs(JTabbedPane tabs) {
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(APP_BACKGROUND);
        tabs.setForeground(TEXT_PRIMARY);
    }

    public static final class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            int value = Math.max(1, radius / 6);
            insets.set(value, value, value, value);
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            int offset = Math.max(1, getBorderInsets(c).left / 2);
            g2.drawRoundRect(x + offset, y + offset, width - (offset * 2) - 1, height - (offset * 2) - 1, radius, radius);
            g2.dispose();
        }
    }

    private static final class FilledButtonUI extends BasicButtonUI {
        private final Color background;
        private final Color foreground;

        private FilledButtonUI(Color background, Color foreground) {
            this.background = background;
            this.foreground = foreground;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = Math.min(c.getHeight() - 1, 28);
            Color fill = button.isEnabled() ? background : new Color(203, 213, 225);
            if (button.getModel().isPressed()) {
                fill = fill.darker();
            } else if (button.getModel().isRollover()) {
                fill = blend(fill, Color.WHITE, 0.08f);
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);

            g2.setColor(button.isEnabled() ? blend(fill, Color.BLACK, 0.12f) : new Color(203, 213, 225));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, arc, arc);
            g2.dispose();

            button.setForeground(button.isEnabled() ? foreground : new Color(100, 116, 139));
            super.paint(g, c);
        }
    }

    private static Color blend(Color base, Color mix, float ratio) {
        float clamped = Math.max(0f, Math.min(1f, ratio));
        int r = Math.round(base.getRed() * (1 - clamped) + mix.getRed() * clamped);
        int g = Math.round(base.getGreen() * (1 - clamped) + mix.getGreen() * clamped);
        int b = Math.round(base.getBlue() * (1 - clamped) + mix.getBlue() * clamped);
        return new Color(r, g, b);
    }
}
