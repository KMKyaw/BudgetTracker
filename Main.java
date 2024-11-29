import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BudgetTracker tracker = new BudgetTracker();
        String command;

        System.out.println("\nWelcome to KM's Budget Tracker!");
        System.out.println("Type 'help' for a list of commands.");

        while (true) {
            System.out.print("\n> ");
            command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "add":
                    if (tracker.getDailyBudget() == 0) {
                        System.out.println("Please set your daily budget first using the 'set-budget' command.");
                        break;
                    }
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine();
                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter amount: ");
                    int amount = Integer.parseInt(scanner.nextLine());
                    tracker.addExpense(category, description, amount);
                    break;

                case "daily":
                    if (tracker.getDailyBudget() == 0) {
                        System.out.println("Please set your daily budget first using the 'set-budget' command.");
                    } else {
                        tracker.showDailySummary();
                    }
                    break;

                case "monthly":
                    tracker.showMonthlySummary();
                    break;

                case "export":
                    System.out.print("Enter filename for export (e.g., expenses.csv): ");
                    String fileName = scanner.nextLine();
                    tracker.exportToCSV(fileName);
                    break;

                case "set-budget":
                    System.out.print("Enter your new daily budget (baht): ");
                    int newBudget = Integer.parseInt(scanner.nextLine());
                    tracker.setDailyBudget(newBudget);
                    break;

                case "help":
                    showHelp();
                    break;

                case "exit":
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid command. Try: 'help' for available commands.");
            }
        }
    }

    private static void showHelp() {
        System.out.println("\n--- Available Commands ---");
        System.out.println("add            : Add a new expense to your daily budget.");
        System.out.println("daily          : Show today's spending summary.");
        System.out.println("monthly        : Show the spending summary for the current month.");
        System.out.println("export         : Export your expenses to a CSV file.");
        System.out.println("set-budget     : Set or update your daily budget.");
        System.out.println("help           : Display this help message.");
        System.out.println("exit           : Exit the program.");
        System.out.println("\nUse the commands above to track and manage your daily expenses!");
    }
}
