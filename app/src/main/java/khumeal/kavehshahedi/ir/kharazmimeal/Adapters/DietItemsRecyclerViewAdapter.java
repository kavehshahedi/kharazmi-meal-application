package khumeal.kavehshahedi.ir.kharazmimeal.Adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import khumeal.kavehshahedi.ir.kharazmimeal.Models.DietModel;
import khumeal.kavehshahedi.ir.kharazmimeal.R;

public class DietItemsRecyclerViewAdapter extends RecyclerView.Adapter<DietItemsRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<DietModel> models;

    public DietItemsRecyclerViewAdapter(Context ctx, List<DietModel> mdls)
    {
        this.context = ctx;
        this.models = mdls;
    }

    @Override
    public DietItemsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DietItemsRecyclerViewAdapter.ViewHolder holder, int position)
    {
        holder.dayNameText.setText(models.get(position).getDayName());

        StringBuilder breakfast = new StringBuilder();
        StringBuilder lunch = new StringBuilder();
        StringBuilder dinner = new StringBuilder();

        for (int i = 0; i < models.get(position).getBreakfastList().size(); i++)
        {
            if (models.get(position).getBreakfastList().get(i).equals("null"))
            {
                breakfast.append("");
            }else
            {
                int num = i+1;
                breakfast.append(String.valueOf(num)).append(". ").append(models.get(position).getBreakfastList().get(i)).append("\n");
            }

            //Log.e("Breakfast : ",models.get(position).getBreakfastList().get(0));
        }

        for (int i = 0; i < models.get(position).getLunchList().size(); i++)
        {
            if (models.get(position).getLunchList().get(i).equals("null"))
            {
                lunch.append("");
            }
            else {
                int num = i + 1;
                lunch.append(String.valueOf(num)).append(". ").append(models.get(position).getLunchList().get(i)).append("\n");
            }
        }

        for (int i = 0; i < models.get(position).getDinnerList().size(); i++)
        {
            if (models.get(position).getDinnerList().get(i).equals("null"))
            {
                dinner.append("");
            }else {
                int num = i + 1;
                dinner.append(String.valueOf(num)).append(". ").append(models.get(position).getDinnerList().get(i)).append("\n");
            }
        }

        holder.breakfastText.setText(breakfast);
        holder.lunchText.setText(lunch);
        holder.dinnerText.setText(dinner);


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public AppCompatTextView dayNameText;

        public AppCompatTextView breakfastText;
        public AppCompatTextView lunchText;
        public AppCompatTextView dinnerText;

        public ViewHolder(View itemView)
        {
            super(itemView);
            dayNameText = itemView.findViewById(R.id.dietDayNameTextView);

            breakfastText = itemView.findViewById(R.id.dietPopupBreakfastValueTextView);
            lunchText = itemView.findViewById(R.id.dietPopupLunchValueTextView);
            dinnerText = itemView.findViewById(R.id.dietPopupDinnerValueTextView);
        }
    }
}
