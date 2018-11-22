package khumeal.kavehshahedi.ir.kharazmimeal.Models;

import java.util.List;

public class DietModel
{
    String dayName;
    List<String> breakfastList,lunchList,dinnerList;

    public DietModel(String dayName, List<String> breakfastList, List<String> lunchList, List<String> dinnerList) {
        this.dayName = dayName;
        this.breakfastList = breakfastList;
        this.lunchList = lunchList;
        this.dinnerList = dinnerList;
    }

    public String getDayName() {
        return dayName;
    }

    public List<String> getBreakfastList() {
        return breakfastList;
    }

    public List<String> getLunchList() {
        return lunchList;
    }

    public List<String> getDinnerList() {
        return dinnerList;
    }
}
