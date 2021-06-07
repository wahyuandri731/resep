package model;

public class RecipeModel {
    public String imgUrl;
    public String recipeName;
    public String categoryName;
    public String ingredients;
    public String howto;
    public int categoryId;
    public int recipeId;

    public RecipeModel(String imgUrl, String name, String category, int catId, int recId) {
        this.imgUrl = imgUrl;
        this.recipeName = name;
        this.categoryName = category;
        this.categoryId = catId;
        this.recipeId = recId;
    }
}
