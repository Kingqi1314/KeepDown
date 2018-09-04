package com.test.kingqi.keepdown;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends BaseActivity
    implements CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener
                                                {
    private static final String TAG = "MainActivity";
    private TextView mTextMonthDay;
    private TextView mTextYear;
    private TextView mTextLunar;
    private TextView mTextCurrentDay;
    private CalendarView calendarView;
    private Calendar calendar;
    private int mYear;
    private List<Message> currentMonthList;
    private CalendarLayout calendarLayout;
    private EditText editText;
    private CardView cardView;
    private boolean editFlag=false;
    private int offSetY=0;
    private int edit_text_origin_height;
    private int colors[]={0xFF40db25,0xFFe69138,0xFFdf1356,0xFFedc56d,0xFFaacc44,0xFFbc13f0};
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
    //初始化数据：
    @Override
    protected void initData() {
        int year=calendar.getYear();
        int month=calendar.getMonth();
        currentMonthList= DataSupport.where("timeYear=? and timeMonth=?",String.valueOf(year),String.valueOf(month)).find(Message.class);
        if (currentMonthList.size()==0){
            return;
        }
        Map<String, Calendar> map = new HashMap<>();
        for (Message message:currentMonthList){
            map.put(getSchemeCalendar(year,month,Integer.parseInt(message.getTimeDay()),colors[(int) (Math.random()*6)],message.getTag()).toString(),
                    getSchemeCalendar(year,month,Integer.parseInt(message.getTimeDay()),colors[(int) (Math.random()*6)],message.getTag())
            );
            if (Integer.parseInt(message.getTimeDay())==calendar.getDay()){
                editText.setText(message.getText());
            }
        }
        calendarView.setSchemeDate(map);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
    //初始化界面
    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        setStatusBarDarkMode();
        mTextMonthDay=(TextView)findViewById(R.id.tv_month_day);
        mTextYear=(TextView)findViewById(R.id.tv_year);
        mTextLunar=(TextView)findViewById(R.id.tv_lunar);
        calendarView=(CalendarView)findViewById(R.id.calendarView);
        calendar=calendarView.getSelectedCalendar();
        calendarLayout=(CalendarLayout)findViewById(R.id.calendarLayout);
        mTextCurrentDay=(TextView)findViewById(R.id.tv_current_day);
        calendarView.setOnCalendarSelectListener(this);
        calendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(calendarView.getCurYear()));
        mYear=calendarView.getCurYear();
        mTextMonthDay.setText(calendarView.getCurMonth()+"月"+calendarView.getCurDay()+"日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(calendarView.getCurDay()));
        editText=(EditText)findViewById(R.id.text);
        cardView=(CardView)findViewById(R.id.cardView);
        addActionListener();
    }
    //添加各种监听
    private void addActionListener(){
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                calendar=new Calendar();
                calendar.setYear(year);
                calendar.setMonth(month);
                initData();
            }
        });
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                MainActivity.this.calendar=calendar;
                for (Message message:currentMonthList){
                    if (message.getTimeDay().equals(String.valueOf(calendar.getDay()))){
                        editText.setText(message.getText());
                        return;
                    }
                }
                editText.setText(null);
            }
        });
        calendarView.setOnCalendarLongClickListener(new CalendarView.OnCalendarLongClickListener() {
            @Override
            public void onCalendarLongClickOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarLongClick(final Calendar calendar) {
                final String[] strings={"例假","日记","假期","节日","其它"};
                AlertDialog dialog=new AlertDialog.Builder(MainActivity.this)
                        .setItems(strings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Message message=new Message();
                                Message.Time time=new Message.Time(calendar.getYear(),calendar.getMonth(),calendar.getDay());
                                message.setTime(time);
                                switch (i){
                                    case 0:
                                        message.setTag("经");
                                        break;
                                    case 1:
                                        message.setTag("记");
                                        break;
                                    case 2:
                                        message.setTag("假");
                                        break;
                                    case 3:
                                        message.setTag("节");
                                        break;
                                    case 4:
                                        message.setTag("其");
                                        break;
                                        default:
                                            break;
                                }
                                saveOnInternet(save(message));
                            }
                        })
                        .create();
                dialog.show();
            }
        });
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!calendarLayout.isExpand()){
                    calendarView.showYearSelectLayout(mYear);
                    return;
                }
                calendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.scrollToCurrent(true);
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b&&!editFlag){
                    hideCalendar();
                }
            }
        });
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth()+"月"+calendar.getDay()+"日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear=calendar.getYear();
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }
    private void hideCalendar(){
        offSetY=calendarView.getBottom();
        editText.clearFocus();
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(calendarView,"translationY",0,-calendarView.getHeight()-5);
        ObjectAnimator objectAnimator1=ObjectAnimator.ofFloat(calendarView,"alpha",1,0);
        ObjectAnimator objectAnimator2=ObjectAnimator.ofFloat(cardView,"translationY",-offSetY);
        edit_text_origin_height=cardView.getHeight();
        ValueAnimator valueAnimator=ValueAnimator.ofInt(edit_text_origin_height,edit_text_origin_height+offSetY/2);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int h=(Integer)valueAnimator.getAnimatedValue();
                cardView.getLayoutParams().height=h;
                cardView.requestLayout();
            }
        });
        AnimatorSet set=new AnimatorSet();
        set.setDuration(800);
        set.setInterpolator(new OvershootInterpolator());
        set.play(objectAnimator).with(objectAnimator1).with(objectAnimator2).with(valueAnimator);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                editFlag=true;
                editText.requestFocus();
                mTextMonthDay.setClickable(false);
                //弹出键盘：
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void showCalendar(){
        editText.clearFocus();
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(calendarView,"translationY",0);
        ObjectAnimator objectAnimator1=ObjectAnimator.ofFloat(calendarView,"alpha",0,1);
        ObjectAnimator objectAnimator2=ObjectAnimator.ofFloat(cardView,"translationY",0);
        ValueAnimator valueAnimator=ValueAnimator.ofInt(edit_text_origin_height+offSetY/2,edit_text_origin_height);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int h=(Integer)valueAnimator.getAnimatedValue();
                cardView.getLayoutParams().height=h;
                cardView.requestLayout();
            }
        });
        AnimatorSet set=new AnimatorSet();
        set.setDuration(800);
        set.setInterpolator(new OvershootInterpolator());
        set.play(objectAnimator).with(objectAnimator1).with(objectAnimator2).with(valueAnimator);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                editFlag=false;
                mTextMonthDay.setClickable(true);
                ViewGroup.LayoutParams layoutParams=cardView.getLayoutParams();
                layoutParams.height= ViewGroup.LayoutParams.MATCH_PARENT;
                cardView.setLayoutParams(layoutParams);
                //收起键盘：
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (editFlag){
            showCalendar();
            Message message=new Message();
            message.setTime(new Message.Time(calendar.getYear(),calendar.getMonth(),calendar.getDay()));
            String data=editText.getText().toString().trim();
            if (data.equals("")){
                return;
            }
            message.setText(data);
            saveOnInternet(save(message));
            return;
        }
        super.onBackPressed();
    }
    //保存并上传写的信息
    private void saveOnInternet(final Message message){
        String data=message.getTag()+"\n"+message.getText();
        if (data.equals("\n"))
            return;
        message.setText(data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10,TimeUnit.SECONDS)
                            .writeTimeout(10,TimeUnit.SECONDS)
                            .build();
                    final Gson gson=new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .setPrettyPrinting()
                            .create();
                    String string=gson.toJson(message,Message.class);
                    Log.d(TAG, "run: "+string);
                    RequestBody requestBody=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),string);
                    Request request=new Request.Builder()
                            .post(requestBody)
                            .url("http://118.25.71.102:10086")
                            .build();
                    Response response=client.newCall(request).execute();
                    JSONObject object=new JSONObject(response.body().string());
                    if (object.getString("text").equals("saved")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(MainActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(MainActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private Message save(Message message){
        List<Message> list=DataSupport.where("timeYear=? and timeMonth=? and timeDay=? ",calendar.getYear()+"",calendar.getMonth()+"",calendar.getDay()+"").find(Message.class);
        Message mMessage=null;
        if (list==null||list.size()==0){
            mMessage=new Message();
            if (!TextUtils.isEmpty(message.getTag()))
                mMessage.setTag(message.getTag());
            if (!TextUtils.isEmpty(message.getText()))
                mMessage.setText(message.getText());
            if (message.getTime()!=null){
                mMessage.setTime(message.getTime());
            }
            mMessage.save();
            Log.d(TAG, "save: 保存成功"+message.getText());
            initData();
        }else {
            mMessage=list.get(0);
            if (!TextUtils.isEmpty(message.getTag()))
                mMessage.setTag(message.getTag());
            if (!TextUtils.isEmpty(message.getText()))
                mMessage.setText(message.getText());
            if (message.getTime()!=null){
                mMessage.setTime(message.getTime());
            }
            Message.Time time=message.getTime();
            mMessage.updateAll("timeYear=? and timeMonth=? and timeDay=? ",time.getYear()+"",time.getMonth()+"",time.getDay()+"");
            initData();
        }
        return mMessage;
    }
}
