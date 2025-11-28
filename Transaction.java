import java.time.LocalDate;

public class Transaction {
    public enum Type { INCOME, EXPENSE }

    private Type type;
    private String category;
    private String description;
    private double amount;
    private LocalDate date;

    public Transaction(Type type, String category, String description, double amount, LocalDate date) {
        this.type = type;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public Type getType() { return type; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}
