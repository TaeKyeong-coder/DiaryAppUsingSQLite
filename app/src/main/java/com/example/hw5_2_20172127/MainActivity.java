package com.example.hw5_2_20172127;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    OpenHelper openHelper;
    DatePicker datePicker;
    EditText editDiary;
    Button buttonWrite;
    SQLiteDatabase sqlDB;

    String diaryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("연습문제 12-6");

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        editDiary = (EditText) findViewById(R.id.editDiary);
        buttonWrite = (Button) findViewById(R.id.buttonWrite);

        openHelper = new OpenHelper(this);

        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cDay = cal.get(Calendar.DAY_OF_MONTH);

        diaryData = Integer.toString(cYear) + "_" + Integer.toString(cMonth+1) + "_" +
                Integer.toString(cDay);
        String str = readDiary(diaryData);
        editDiary.setText(str);
        datePicker.init(cYear, cMonth, cDay, new
                DatePicker.OnDateChangedListener() {
                    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                        diaryData = Integer.toString(year) + "_" +
                                Integer.toString(monthOfYear + 1) + "_" + Integer.toString(dayOfMonth);
                        String str = readDiary(diaryData);
                        editDiary.setText(str);
                    }
                });

        buttonWrite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String content = editDiary.getText().toString();
                if (readDiary(diaryData) == null) {
                    sqlDB = openHelper.getWritableDatabase();
                    sqlDB.execSQL("INSERT INTO myDiary VALUES ( '" + diaryData
                            + "', '" + content + "');");
                    sqlDB.close();
                    editDiary.setText(content);
                    if (editDiary.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), diaryData + " 일기 저장", Toast.LENGTH_SHORT).show();
                                buttonWrite.setText("새로 저장");
                    } else {
                        Toast.makeText(getApplicationContext(), diaryData + " 일기 저장", Toast.LENGTH_SHORT).show();
                                buttonWrite.setText("수정 하기");
                    }
                } else {
                    sqlDB = openHelper.getWritableDatabase();
                    sqlDB.execSQL("UPDATE myDiary SET content ='" + content + "' WHERE diaryData = '" + diaryData + "';");
                    sqlDB.close();
                    editDiary.setText(content);
                    if (editDiary.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), diaryData + " 일기 수정", Toast.LENGTH_SHORT).show();
                                buttonWrite.setText("새로 저장");
                    } else {
                        Toast.makeText(getApplicationContext(), diaryData + " 일기 수정", Toast.LENGTH_SHORT).show();
                                buttonWrite.setText("수정 하기");
                    }
                }
            }
        });
    }
    String readDiary(String diaryData) {
        sqlDB = openHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT content FROM myDiary WHERE diaryData='" + diaryData + "';", null);
        String content = null;
        while (cursor.moveToNext()) {
            content = cursor.getString(0);
        }
        if (content != null) {
            editDiary.setText(content);
            if (editDiary.getText().toString().equals("")) {
                editDiary.setText("");
                editDiary.setHint("일기 없음");
                buttonWrite.setText("새로 저장");
            } else {
                buttonWrite.setText("수정 하기");
            }
        } else {
            editDiary.setText("");
            editDiary.setHint("일기 없음");
            buttonWrite.setText("새로 저장");
        }
        cursor.close();
        sqlDB.close();
        return content;
    }
    public class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context) {
            super(context, "myDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE myDiary ( diaryData CHAR(10) PRIMARY KEY, content VARCHAR(500));");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS myDiary");
            onCreate(db);
        }
    }
}

