package khumeal.kavehshahedi.ir.kharazmimeal.Models;

public class FoodModel
{
    String foodNumber,foodName,foodPrice;

    public FoodModel(String foodNumber, String foodName, String foodPrice) {
        this.foodNumber = foodNumber;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
    }

    public String getFoodNumber() {
        return foodNumber;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }
}
