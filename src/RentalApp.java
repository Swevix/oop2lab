import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Locale;

public final class RentalApp extends JFrame {
    final JComboBox<RentalCalculator.BikeType> bikeType =
            new JComboBox<>(RentalCalculator.BikeType.values());
    final JTextField hours = new JTextField("2");
    final JTextField count = new JTextField("1");
    final JCheckBox helmet = new JCheckBox("Шлем — 50 ₽ за велосипед на весь срок");
    final JCheckBox student = new JCheckBox("Студенческая скидка — 10% на прокат");
    final JButton calculate = new JButton("Рассчитать стоимость");
    final JButton reset = new JButton("Сбросить");
    final JLabel total = new JLabel("—", SwingConstants.RIGHT);
    final JLabel status = new JLabel("Укажите параметры и нажмите «Рассчитать стоимость».");
    private final JLabel rentalValue = valueLabel();
    private final JLabel equipmentValue = valueLabel();
    private final JLabel discountValue = valueLabel();
    private final JLabel rate = new JLabel();
    private final RentalCalculator calculator = new RentalCalculator();

    public RentalApp() {
        super("ВелоПрокат • Лабораторная работа № 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 660);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 22));
        root.setBorder(new EmptyBorder(28, 32, 22, 32));
        root.setBackground(new Color(244, 247, 251));
        setContentPane(root);
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 8));
        heading.setOpaque(false);
        JLabel title = new JLabel("ВелоПрокат");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(new Color(25, 55, 85));
        heading.add(title);
        heading.add(new JLabel("Калькулятор стоимости аренды • выберите велосипед и условия"));
        root.add(heading, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 22, 0));
        body.setOpaque(false);
        JPanel form = card();
        form.setLayout(new GridLayout(11, 1, 0, 5));
        form.add(new JLabel("Тип велосипеда"));
        form.add(bikeType);
        form.add(rate);
        form.add(new JLabel("Время проката, часов (1–24)"));
        form.add(hours);
        form.add(new JLabel("Количество велосипедов (1–20)"));
        form.add(count);
        helmet.setOpaque(false);
        student.setOpaque(false);
        form.add(helmet);
        form.add(student);
        form.add(calculate);
        form.add(reset);
        body.add(form);

        JPanel receipt = card();
        receipt.setLayout(new BorderLayout(0, 16));
        JLabel receiptTitle = new JLabel("Ваш расчёт");
        receiptTitle.setFont(new Font("SansSerif", Font.BOLD, 21));
        receipt.add(receiptTitle, BorderLayout.NORTH);
        JPanel lines = new JPanel(new GridLayout(8, 1, 0, 8));
        lines.setOpaque(false);
        lines.add(new JLabel("Прокат велосипедов")); lines.add(rentalValue);
        lines.add(new JLabel("Шлемы")); lines.add(equipmentValue);
        lines.add(new JLabel("Скидка на прокат")); lines.add(discountValue);
        lines.add(new JLabel("ИТОГО К ОПЛАТЕ"));
        total.setFont(new Font("SansSerif", Font.BOLD, 32));
        total.setForeground(new Color(20, 120, 95));
        lines.add(total);
        receipt.add(lines, BorderLayout.CENTER);
        receipt.add(new JLabel("Все цены указаны в рублях."), BorderLayout.SOUTH);
        body.add(receipt);
        root.add(body, BorderLayout.CENTER);
        status.setBorder(new EmptyBorder(6, 0, 0, 0));
        root.add(status, BorderLayout.SOUTH);

        calculate.addActionListener(event -> calculatePrice());
        reset.addActionListener(event -> resetForm());
        bikeType.addActionListener(event -> updateRate());
        javax.swing.event.DocumentListener changes = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { invalidateResult(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { invalidateResult(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { invalidateResult(); }
        };
        hours.getDocument().addDocumentListener(changes);
        count.getDocument().addDocumentListener(changes);
        helmet.addActionListener(event -> invalidateResult());
        student.addActionListener(event -> invalidateResult());
        getRootPane().setDefaultButton(calculate);
        updateRate();
    }

    private static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    private static JLabel valueLabel() {
        JLabel label = new JLabel("—", SwingConstants.RIGHT);
        label.setFont(new Font("SansSerif", Font.BOLD, 19));
        return label;
    }

    private String money(BigDecimal value) {
        return String.format(Locale.forLanguageTag("ru-RU"), "%,.2f ₽", value);
    }

    private void updateRate() {
        RentalCalculator.BikeType selected = (RentalCalculator.BikeType) bikeType.getSelectedItem();
        rate.setText("Тариф: " + selected.getHourlyRate() + " ₽ / час за велосипед");
        invalidateResult();
    }

    private void invalidateResult() {
        rentalValue.setText("—"); equipmentValue.setText("—");
        discountValue.setText("—"); total.setText("—");
        status.setForeground(new Color(80, 95, 110));
        status.setText("Укажите параметры и нажмите «Рассчитать стоимость».");
    }

    private void calculatePrice() {
        invalidateResult();
        try {
            int h;
            int n;
            try {
                h = Integer.parseInt(hours.getText().trim());
                n = Integer.parseInt(count.getText().trim());
            } catch (NumberFormatException error) {
                throw new IllegalArgumentException("Введите целые числа: часы 1–24, количество 1–20.");
            }
            RentalCalculator.Result result = calculator.calculate(
                    (RentalCalculator.BikeType) bikeType.getSelectedItem(), h, n,
                    helmet.isSelected(), student.isSelected());
            rentalValue.setText(money(result.rental()));
            equipmentValue.setText(money(result.equipment()));
            discountValue.setText("− " + money(result.discount()));
            total.setText(money(result.total()));
            status.setForeground(new Color(20, 120, 95));
            status.setText("Расчёт готов: " + n + " вел. × " + h + " ч. Тариф и условия учтены.");
        } catch (IllegalArgumentException error) {
            status.setForeground(new Color(185, 45, 45));
            status.setText(error.getMessage());
        }
    }

    private void resetForm() {
        bikeType.setSelectedIndex(0); hours.setText("2"); count.setText("1");
        helmet.setSelected(false); student.setSelected(false); invalidateResult();
    }
}
