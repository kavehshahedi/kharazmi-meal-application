package khumeal.kavehshahedi.ir.kharazmimeal.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import khumeal.kavehshahedi.ir.kharazmimeal.Models.FoodModel;
import khumeal.kavehshahedi.ir.kharazmimeal.Models.SelfDataModel;
import khumeal.kavehshahedi.ir.kharazmimeal.R;
import khumeal.kavehshahedi.ir.kharazmimeal.ReserveActivity;
import mehdi.sakout.fancybuttons.FancyButton;

public class DayItemsRecyclerViewAdapter extends RecyclerView.Adapter<DayItemsRecyclerViewAdapter.ViewHolder>
{

    Context context;
    List<SelfDataModel> models;
    int pos;
    int mode;

    public DayItemsRecyclerViewAdapter(Context ctx, List<SelfDataModel> mdls)
    {
        this.context = ctx;
        this.models = mdls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_section_card,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.dayNameText.setText(models.get(position).getDayName());
        holder.dayDateText.setText(models.get(position).getDayDate());

        if(!models.get(position).getBreakfastName().equals(""))
        {
            holder.breakfastName.setVisibility(View.VISIBLE);
            holder.breakfastButton.setVisibility(View.INVISIBLE);
            holder.breakfastName.setText(models.get(position).getBreakfastName());
        }else {
            holder.breakfastName.setVisibility(View.INVISIBLE);
            holder.breakfastButton.setVisibility(View.VISIBLE);
            holder.breakfastButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Breakfast Link Button Action
                }
            });
        }

        if(!models.get(position).getLunchName().equals(""))
        {
            holder.lunchName.setVisibility(View.VISIBLE);
            holder.lunchButton.setVisibility(View.INVISIBLE);
            holder.lunchName.setText(models.get(position).getLunchName());
        }else {
            holder.lunchName.setVisibility(View.INVISIBLE);
            holder.lunchButton.setVisibility(View.VISIBLE);
            holder.lunchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Lunch Link Button Action
                }
            });
        }

        if(!models.get(position).getDinnerName().equals(""))
        {
            holder.dinnerName.setVisibility(View.VISIBLE);
            holder.dinnerButton.setVisibility(View.INVISIBLE);
            holder.dinnerName.setText(models.get(position).getDinnerName());
        }else {
            holder.dinnerName.setVisibility(View.INVISIBLE);
            holder.dinnerButton.setVisibility(View.VISIBLE);
        }

        //Check Link Availability
        if (!models.get(position).getBreakfastLink().equals("null"))
        {
            holder.breakfastButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Breakfast Link Button Action
                    try {
                        pos = position;
                        mode = 1;
                        new GetFoodsDataWebsite(models.get(position).getBreakfastLink()).execute();
                    } catch (Exception e) {e.printStackTrace();}
                }
            });
        }else
        {
            holder.breakfastButton.setBackgroundColor(Color.RED);
            holder.breakfastButtonText.setText("عدم امکان رزرو");
        }

        if (!models.get(position).getLunchLink().equals("null"))
        {
            holder.lunchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Lunch Link Button Action
                    try {
                        pos = position;
                        mode = 2;
                        new GetFoodsDataWebsite(models.get(position).getLunchLink()).execute();
                    } catch (Exception e) {e.printStackTrace();}
                }
            });
        }else
        {
            holder.lunchButton.setBackgroundColor(Color.RED);
            holder.lunchButtonText.setText("عدم امکان رزرو");
        }

        if (!models.get(position).getDinnerLink().equals("null"))
        {
            holder.dinnerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Dinner Link Button Action
                    try {
                        pos = position;
                        mode = 3;
                        new GetFoodsDataWebsite(models.get(position).getDinnerLink()).execute();
                    } catch (Exception e) {e.printStackTrace();}
                }
            });
        }else
        {
            holder.dinnerButton.setBackgroundColor(Color.RED);
            holder.dinnerButtonText.setText("عدم امکان رزرو");
        }

    }


    List<FoodModel> foodModels = new ArrayList<>();

    Thread mainThread = new Thread();

    Dialog popup;
    ConstraintLayout loadingLayout;
    ConstraintLayout foodLayout;

    FancyButton selfButton;
    AppCompatTextView selfText;
    int selfNumber = 0;

    FancyButton foodButton;
    AppCompatTextView foodText;
    long foodPrice = 0;
    int foodNumber = 0;

    AppCompatTextView foodErrorText;

    FancyButton reserveButton;

    public void openPopup()
    {
        foodModels.clear();

        popup = new Dialog(context);
        popup.setContentView(R.layout.food_select_popup);
        popup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (Build.VERSION.SDK_INT <= 18)
        {
            int dividerID = popup.getContext().getResources()
                    .getIdentifier("android:id/titleDivider", null, null);
            View divider = popup.findViewById(dividerID);
            divider.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
        loadingLayout = popup.findViewById(R.id.popupLoadingView);
        foodLayout = popup.findViewById(R.id.popupMenuView);

        selfButton = popup.findViewById(R.id.selfSelectionButton);
        selfText = popup.findViewById(R.id.selectedSelfTextView);

        foodButton = popup.findViewById(R.id.foodSelectionButton);
        foodText = popup.findViewById(R.id.selectedFoodTextView);

        reserveButton = popup.findViewById(R.id.reserveFoodButton);

        foodErrorText = popup.findViewById(R.id.foodReserveErrorTextView);
        foodErrorText.setVisibility(View.INVISIBLE);

        selfText.setText("");
        foodText.setText("");

        selectSelf(1);

        /*final String[] selves = new String[4];
        selves[0] = "خواهران";
        selves[1] = "کارمندان";
        selves[2] = "برادران";
        selves[3] = "رستوران آزاد";

        selfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("انتخاب سلف")
                        .items(selves)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                            {
                                selectSelf(which,text);
                                return true;
                            }
                        })
                        .positiveText("انتخاب")
                        .onPositive(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                            {

                            }
                        })
                        .typeface(ResourcesCompat.getFont(context,R.font.isbold),ResourcesCompat.getFont(context,R.font.isbold))
                        .itemsColor(Color.parseColor("#212121"))
                        .widgetColor(Color.parseColor("#ff8109"))
                        .positiveColor(Color.parseColor("#ff8109"))
                        .itemsGravity(GravityEnum.END)
                        .buttonsGravity(GravityEnum.CENTER)
                        .titleGravity(GravityEnum.END)
                        .show();
            }
        });*/

        popup.show();
    }

    private void selectSelf(int which)
    {
        selfText.setText("");
        Document document = Jsoup.parse(ReserveActivity.reserveHTML);
        String self = document.select("select[name=RD_Self] option[selected]").text();
        String selfNumS = self.replaceAll("-.*","");
        selfNumS = selfNumS.replaceAll("\\s+","");
        int selfNum = Integer.parseInt(selfNumS);
        String selfName = self.replaceAll(".*-","").replace("\\s+","");
        selfNumber = selfNum;
        selfText.setText(self);
    }

    private void selectFood(int which, CharSequence text)
    {
        foodText.setText("");
        foodText.setText(text);
        foodNumber = which + 1;
    }

    public void showFoodData()
    {
        final List<String> foods = new ArrayList<>();
        for (int i = 0; i < foodModels.size(); i++)
        {
            foods.add(foodModels.get(i).getFoodName() + " - " + foodModels.get(i).getFoodPrice() + " ریال");
        }

        final String[] foodsArray = foods.toArray(new String[foods.size()]);

        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("انتخاب غذا")
                        .items(foodsArray)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                            {
                                if (text != null)
                                {
                                    String s = text.toString();
                                    String s1 = s;
                                    s = s.replaceAll(".*-","");
                                    s = s.replaceAll("\\s+","");
                                    s = s.replace("ریال","");
                                    foodPrice = Long.parseLong(s);
                                    s1 = s1.replaceAll("-.*","");
                                    selectFood(which,s1);
                                }
                                return true;
                            }
                        })
                        .positiveText("انتخاب")
                        .onPositive(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                            {

                            }
                        })
                        .typeface(ResourcesCompat.getFont(context,R.font.isbold),ResourcesCompat.getFont(context,R.font.isbold))
                        .itemsColor(Color.parseColor("#212121"))
                        .widgetColor(Color.parseColor("#ff8109"))
                        .positiveColor(Color.parseColor("#ff8109"))
                        .itemsGravity(GravityEnum.START)
                        .buttonsGravity(GravityEnum.CENTER)
                        .titleGravity(GravityEnum.END)
                        .show();
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (canReserve())
                {

                    switch (mode)
                    {
                        case 1 :
                        {
                            String s = models.get(pos).getBreakfastType();
                            s = s.substring(s.length() - 1);
                            s = s.replaceAll("\\s+","");
                            Log.e("Reserve : ","Num" + foodNumber + "Type" + s + " Food Price" + foodPrice);
                            ReserveActivity.web.loadUrl("javascript:SelectGh(" + foodNumber + ",0,'name'," + s + "," + foodPrice + ");");
                            ReserveActivity.web.loadUrl("javascript:var self = document.getElementById('" + models.get(pos).getBreakfastSelf() + "').value='" + selfNumber + "';");
                            ReserveActivity.web.loadUrl("javascript:var buttonlogin = document.getElementById('btn_saveKharid').click();");
                            break;
                        }

                        case 2 :
                        {
                            String s = models.get(pos).getLunchType();
                            s = s.substring(s.length() - 1);
                            s = s.replaceAll("\\s+","");
                            Log.e("Reserve : ","Num" + foodNumber + "Type" + s + " Food Price" + foodPrice);
                            ReserveActivity.web.loadUrl("javascript:SelectGh(" + foodNumber + ",1,'name'," + s + "," + foodPrice + ");");
                            ReserveActivity.web.loadUrl("javascript:var self = document.getElementById('" + models.get(pos).getLunchSelf() + "').value='" + selfNumber + "';");
                            ReserveActivity.web.loadUrl("javascript:var buttonlogin = document.getElementById('btn_saveKharid').click();");
                            break;
                        }

                        case 3 :
                        {
                            String s = models.get(pos).getDinnerType();
                            s = s.substring(s.length() - 1);
                            //Log.e("Code : ","SelectGh(" + foodNumber + ",1,'name'," + s + "," + foodPrice + ");");
                            s = s.replaceAll("\\s+","");
                            Log.e("Reserve : ","Num" + foodNumber + "Type" + s + " Food Price" + foodPrice);
                            ReserveActivity.web.loadUrl("javascript:SelectGh(" + foodNumber + ",2,'name'," + s + "," + foodPrice + ");");
                            ReserveActivity.web.loadUrl("javascript:var self = document.getElementById('" + models.get(pos).getDinnerSelf() + "').value='" + selfNumber + "';");
                            ReserveActivity.web.loadUrl("javascript:var buttonlogin = document.getElementById('btn_saveKharid').click();");
                            break;
                        }
                        default:break;
                    }

                    popup.dismiss();
                }else
                {
                    boolean hasFood = (foodNumber != 0);
                    boolean hasWallet = (walletValue >= foodPrice);
                    if(!hasFood)
                    {
                        foodErrorText.setText("ابتدا غذا را انتخاب کنید");
                    }else if (!hasWallet)
                    {
                        foodErrorText.setText("اعتبار کافی نیست!");
                    }else if(!hasFood && !hasWallet)
                    {
                        foodErrorText.setText("ابتدا غذا را انتخاب کنید");
                    }
                    foodErrorText.setVisibility(View.VISIBLE);
                }
            }
        });

        loadingLayout.setVisibility(View.INVISIBLE);
        foodLayout.setVisibility(View.VISIBLE);
    }

    Long walletValue;
    public boolean canReserve()
    {
        String wallet = ReserveActivity.getWallet();
        wallet = wallet.replace(",","");
        walletValue = Long.parseLong(wallet);
        return (Long.parseLong(wallet) >= foodPrice) && (selfNumber != 0) && (foodNumber != 0);
    }

    public void getFoodData(String foodLink) throws Exception
    {


        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                foodLayout.setVisibility(View.INVISIBLE);
                loadingLayout.setVisibility(View.VISIBLE);
            }
        });

        foodLink = foodLink.replace("');","");
        foodLink = foodLink.replace("openSearch('","");
        foodLink = foodLink.replace("amp;","");
        foodLink = "http://meal.khu.ac.ir/" + foodLink;

        Document document = Jsoup.connect(foodLink).get();

        Element table = document.getElementById("GhazaGrid");
        Elements rows = table.select("tr");

        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");

            FoodModel model = new FoodModel(cols.get(0).text(),cols.get(2).text(),cols.get(3).text());
            foodModels.add(model);
        }
    }

    @Override
    public int getItemCount()
    {
        return 7;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView dayNameText;
        public TextView dayDateText;

        public FancyButton breakfastButton;
        public FancyButton lunchButton;
        public FancyButton dinnerButton;

        public AppCompatTextView breakfastName;
        public AppCompatTextView lunchName;
        public AppCompatTextView dinnerName;

        public AppCompatTextView breakfastButtonText;
        public AppCompatTextView lunchButtonText;
        public AppCompatTextView dinnerButtonText;

        public ViewHolder(View itemView) {
            super(itemView);
            dayNameText = itemView.findViewById(R.id.dayNameTextView);
            dayDateText = itemView.findViewById(R.id.dayDateTextView);

            breakfastButton = itemView.findViewById(R.id.breakfastSelectButton);
            lunchButton = itemView.findViewById(R.id.lunchSelectButton);
            dinnerButton = itemView.findViewById(R.id.dinnerSelectButton);

            breakfastName = itemView.findViewById(R.id.breakfastTextView);
            lunchName = itemView.findViewById(R.id.lunchTextView);
            dinnerName = itemView.findViewById(R.id.dinnerTextView);

            breakfastButtonText = itemView.findViewById(R.id.breakfastButtonTextView);
            lunchButtonText = itemView.findViewById(R.id.lunchButtonTextView);
            dinnerButtonText = itemView.findViewById(R.id.dinnerButtonTextView);

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetFoodsDataWebsite extends AsyncTask<Void,Void,Void>
    {
        String webLink;
        public GetFoodsDataWebsite(String link)
        {
            webLink = link;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    openPopup();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            try {
                getFoodData(webLink);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showFoodData();
        }
    }
}