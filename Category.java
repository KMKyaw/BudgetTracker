public class Category {
    private String name;
    private int limit;
    private int spent;

    public Category(String name, int limit) {
        this.name = name;
        this.limit = limit;
        this.spent = 0;
    }

    public Category(String name, int limit, int spent) {
        this(name, limit);
        this.spent = spent;
    }

    public void addExpense(int amount) {
        this.spent += amount;
    }

    public String getName() {
        return name;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSpent() {
        return spent;
    }

    @Override
    public String toString() {
        return name + ": Spent " + spent + " baht (Limit: " + limit + " baht)";
    }
}
