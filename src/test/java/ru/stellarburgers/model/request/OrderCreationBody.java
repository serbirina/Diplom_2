package ru.stellarburgers.model.request;

public class OrderCreationBody {
    private String[] ingredients;

    public OrderCreationBody(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public OrderCreationBody() {
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public OrderCreationBody setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}
