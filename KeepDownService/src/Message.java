import com.google.gson.annotations.Expose;

import java.util.Calendar;

public class Message{
    @Expose(serialize = false,deserialize = false)
    private int id;
    @Expose(serialize = false,deserialize = false)
    private Time time;
    @Expose
    private String text;
    @Expose
    private String timeYear;
    @Expose
    private String timeMonth;
    @Expose
    private String timeDay;

    public String getTimeYear() {
        return timeYear;
    }

    public void setTimeYear(String timeYear) {
        this.timeYear = timeYear;
    }

    public String getTimeMonth() {
        return timeMonth;
    }

    public void setTimeMonth(String timeMonth) {
        this.timeMonth = timeMonth;
    }

    public String getTimeDay() {
        return timeDay;
    }

    public void setTimeDay(String timeDay) {
        this.timeDay = timeDay;
    }

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Message(Time time, String text) {
        this.time = time;
        this.text = text;
        this.timeYear=time.getYear()+"";
        this.timeMonth=time.getMonth()+"";
        this.timeDay=time.getDay()+"";
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
        this.timeYear=time.getYear()+"";
        this.timeMonth=time.getMonth()+"";
        this.timeDay=time.getDay()+"";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    static class Time{
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;
        private int seconds;

        public Time(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public Time(int year, int month, int day, int hour, int minute, int seconds) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.seconds = seconds;
        }

        @Override
        public String toString() {
            return "Time{" +
                    "year=" + year +
                    ", month=" + month +
                    ", day=" + day +
                    ", hour=" + hour +
                    ", minute=" + minute +
                    ", seconds=" + seconds +
                    '}';
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }
        public static Message.Time getCurrTime(){
            Calendar calendar=Calendar.getInstance();
            return new Message.Time(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)+1,
                    calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)
            );
        }
        public String getTimeFileName(){
            return year+""+month+""+day+"";
        }
    }
}
