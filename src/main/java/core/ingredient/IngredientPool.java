package core.ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码修复材料池
 */
public class IngredientPool {
    private Map<String, List<Ingredient>> ingredients = new HashMap<>();

    public Map<String, List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public void addAll(Map<String, List<Ingredient>> ing) {
        for (String type : ing.keySet()) {
            List<Ingredient> currentIngredients = this.ingredients.getOrDefault(type, new ArrayList<>());
            currentIngredients.addAll(ing.get(type));
            this.ingredients.put(type, currentIngredients);
        }
    }
}
