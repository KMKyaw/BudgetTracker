import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Expense {
    private String date;  // Will store date in yyyy-MM-dd format
    private String category;
    private String description;
    private int amount;
    private String time;  // Will store time in hh:mm:ss format

    public Expense(String date, String category, String description, int amount, String time) {
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.time = time;
    }

    // Getter methods
    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return date + " " + time + " - " + category + " - " + description + ": " + amount + " baht";
    }

    // Static method to create an Expense object with the current time
    public static Expense fromString(String date, String category, String description, int amount) {
        // Get the current time in hh:mm:ss format
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return new Expense(date, category, description, amount, time);
    }
}
