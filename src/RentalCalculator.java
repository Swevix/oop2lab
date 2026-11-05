import java.math.BigDecimal;
import java.math.RoundingMode;

public final class RentalCalculator {
    public enum BikeType {
        CITY("Городской", 180), MOUNTAIN("Горный", 250), ELECTRIC("Электровелосипед", 400);

        private final String title;
        private final int hourlyRate;

        BikeType(String title, int hourlyRate) {
            this.title = title;
            this.hourlyRate = hourlyRate;
        }

        public int getHourlyRate() { return hourlyRate; }
        @Override public String toString() { return title; }
    }

    public record Result(BigDecimal rental, BigDecimal equipment,
                         BigDecimal discount, BigDecimal total) {}

    public Result calculate(BikeType type, int hours, int count,
                            boolean helmet, boolean student) {
        if (type == null) throw new IllegalArgumentException("Выберите тип велосипеда.");
        if (hours < 1 || hours > 24)
            throw new IllegalArgumentException("Время проката: целое число от 1 до 24 часов.");
        if (count < 1 || count > 20)
            throw new IllegalArgumentException("Количество: целое число от 1 до 20 велосипедов.");
        BigDecimal rental = BigDecimal.valueOf((long) type.getHourlyRate() * hours * count);
        BigDecimal equipment = BigDecimal.valueOf(helmet ? 50L * count : 0);
        BigDecimal discount = student
                ? rental.multiply(new BigDecimal("0.10")) : BigDecimal.ZERO;
        return new Result(money(rental), money(equipment), money(discount),
                money(rental.add(equipment).subtract(discount)));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
