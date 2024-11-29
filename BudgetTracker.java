import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.*;

public class BudgetTracker {
    private int dailyBudget;
    private Map<String, Category> categories;
    private List<Expense> expenses;
    private static final String EXPENSE_FILE = "expenses.txt";
    private static final String BUDGET_FILE = "daily_budget.txt";
    private static final String CATEGORY_FILE = "categories.txt";

    public BudgetTracker() {
        this.expenses = new ArrayList<>();
        this.categories = new HashMap<>();
        this.dailyBudget = loadDailyBudget();
        loadExpenses();
        loadCategories();
    }

    public int getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(int newBudget) {
        this.dailyBudget = newBudget;
        saveDailyBudget();
        System.out.println("Daily budget updated to: " + newBudget + " baht");
    }

    public void addExpense(String category, String description, int amount) {
        String today = LocalDate.now().toString();
        // Use the current time when creating the expense
        Expense expense = Expense.fromString(today, category, description, amount);
        expenses.add(expense);
        saveExpenses();

        // Initialize or update the category in the map
        Category cat = categories.get(category);
        if (cat == null) {
            // If the category doesn't exist, create a new one with no limit (default)
            cat = new Category(category, 0);  // Default limit is 0
            categories.put(category, cat);
        }
        cat.addExpense(amount);  // Update the amount spent in this category
        saveCategories();

        // Calculate remaining daily budget
        int totalSpentToday = expenses.stream()
                .filter(e -> e.getDate().equals(today))
                .mapToInt(Expense::getAmount)
                .sum();
        int remainingBudget = dailyBudget - totalSpentToday;

        System.out.println("\nExpense added: " + expense);
        System.out.println("Remaining budget for today: " + remainingBudget + " baht");

        // Check if category budget is exceeded
        if (cat.getSpent() > cat.getLimit() && cat.getLimit() > 0) {
            System.out.println("Warning: You've exceeded your budget for category: " + category);
        }
    }


    public void showDailySummary() {
        String today = LocalDate.now().toString();
        int totalSpent = expenses.stream()
                .filter(e -> e.getDate().equals(today))
                .mapToInt(Expense::getAmount)
                .sum();
        int remaining = dailyBudget - totalSpent;

        System.out.println("\n--- Today's Summary ---");
        System.out.println("Daily Budget: " + dailyBudget + " baht");
        System.out.println("Spent: " + totalSpent + " baht");
        System.out.println("Remaining: " + remaining + " baht");

        System.out.println("\nDetailed Expenses:");
        expenses.stream()
                .filter(e -> e.getDate().equals(today))
                .forEach(expense -> System.out.println(expense));

        System.out.println("\nCategory Breakdown:");
        for (Map.Entry<String, Category> entry : categories.entrySet()) {
            System.out.println(entry.getKey() + ": Spent " + entry.getValue().getSpent() + " baht");
        }
    }


    public void showMonthlySummary() {
        String currentMonth = LocalDate.now().toString().substring(0, 7);
        Map<String, Integer> categoryTotals = new HashMap<>();
        int totalSpent = 0;

        for (Expense expense : expenses) {
            if (expense.getDate().startsWith(currentMonth)) {
                totalSpent += expense.getAmount();
                categoryTotals.merge(expense.getCategory(), expense.getAmount(), Integer::sum);
            }
        }

        System.out.println("\n--- Monthly Summary ---");
        System.out.println("Total Spent: " + totalSpent + " baht");

        System.out.println("\nCategory Breakdown:");
        for (Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " baht");
        }
    }

    // Persistence Methods
    private void saveExpenses() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXPENSE_FILE))) {
            for (Expense expense : expenses) {
                writer.write(expense.getDate() + "," + expense.getCategory() + "," + expense.getDescription() + "," + expense.getAmount());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving expenses: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(EXPENSE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) { // 5 parts: date, category, description, amount, and time
                    String date = parts[0];
                    String category = parts[1];
                    String description = parts[2];
                    int amount = Integer.parseInt(parts[3]);
                    String time = parts[4];  // Time part added

                    // Create a new Expense object using the updated constructor
                    expenses.add(new Expense(date, category, description, amount, time));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
        }
    }

    private void saveDailyBudget() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BUDGET_FILE))) {
            writer.write(String.valueOf(dailyBudget));
        } catch (IOException e) {
            System.err.println("Error saving daily budget: " + e.getMessage());
        }
    }

    private int loadDailyBudget() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BUDGET_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            System.out.println("No daily budget found. Please set it using 'set-budget' command.");
            return 0;
        }
    }

    private void saveCategories() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CATEGORY_FILE))) {
            for (Category category : categories.values()) {
                writer.write(category.getName() + "," + category.getLimit() + "," + category.getSpent());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving categories: " + e.getMessage());
        }
    }

    private void loadCategories() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CATEGORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    categories.put(parts[0], new Category(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
    }

    public void exportToCSV(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Date,Category,Description,Amount");
            writer.newLine();
            for (Expense expense : expenses) {
                writer.write(expense.getDate() + "," + expense.getCategory() + "," + expense.getDescription() + "," + expense.getAmount());
                writer.newLine();
            }
            System.out.println("Expenses data exported to " + fileName);
        } catch (IOException e) {
            System.err.println("Error exporting expenses data: " + e.getMessage());
        }
    }

}
