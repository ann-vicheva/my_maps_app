package com.example.mymapsapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {



    //для выбора категории
    String[] data = { "Ресторан", "Кафе", "Музей", "Памятник", "Памятник личности", "Парк",
            "Торг. комплекс", "Кинотеатр", "Театр", "Храм/Церковь", "Библиотека",
            "Квест", "Ночной клуб", "Дет. развл. центр", "Отель"};

    String[] data_number = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13"};

    Integer number=0;

    String category_chenge="";

    //Объявим переменные компонентов
    LinearLayout linlay;
    Button button, button1;
    TextView textView;

    //Для вызова GoogleMap
    private double lat;
    private double lng;
    private String mCity;


    List list = new ArrayList();

    int [] shops_number= new int[500];
    int common_count=0;

    String proba_algoritma1;

    int[][] ger=new int[200][200];

    int postroenie_puti=0;

    int bool_value=0;

    //**********for komivoyagera**start
    int maxn=500;                //максимум городов
    int n,i2,s,min1,count,found;        //n-количество городов
    int [] m = new int[maxn];           //m-текущий путь
    int [] minm = new int[maxn];        //minm-минимальный путь
    //**********for komivoyagera**end


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Найдем компоненты в XML разметке
        button = (Button) findViewById(R.id.button);
        button1 = (Button) findViewById(R.id.button1);
        textView = (TextView) findViewById(R.id.textView);
        linlay=(LinearLayout) findViewById(R.id.container_for_textview);

        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }


        // адаптер для выбора категории
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id){

                category_chenge=data[position];
                bool_value=0;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //********************************************************************************
        // адаптер для выбора заведения, до которого строим маршрут
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_number);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner_map = (Spinner) findViewById(R.id.spinner_map);
        spinner_map.setAdapter(adapter1);
        // заголовок
        spinner_map.setPrompt("Title");
        // выделяем элемент
        spinner_map.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner_map.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                number=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(47.8228900, 35.1903100);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Zaporogie"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void Click_M(View view){
        if(postroenie_puti==0)
            Toast.makeText(getBaseContext(), "Необходимо выбрать категорию и алгоритм", Toast.LENGTH_SHORT).show();
        else {
            if (number >= common_count)
                Toast.makeText(getBaseContext(), "Введен неверный номер заведения", Toast.LENGTH_SHORT).show();
            else {
                int buffer;
                buffer = shops_number[number];
                Zavedenie zavedenie1 = (Zavedenie) list.get(buffer);
                lat = zavedenie1.latitude2 / 0.0175;
                lng = zavedenie1.longitude2 / 0.0175;
                mCity = lat + "," + lng;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?daddr=" + mCity));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        }
    }


    public void Click_p(View view){

        if(postroenie_puti==0)
            Toast.makeText(getBaseContext(), "Необходимо выбрать категорию и алгоритм", Toast.LENGTH_SHORT).show();
        else {
            mMap.clear();//для удачной перерисовки карты

            PolygonOptions rectOptions = new PolygonOptions()
                    .strokeColor(Color.BLUE);

            int i = 0;
            while (i < common_count) {
                int buffer1 = shops_number[i];
                Zavedenie zavedenie1 = (Zavedenie) list.get(buffer1);
                LatLng buffer = new LatLng(zavedenie1.latitude2 / 0.0175, zavedenie1.longitude2 / 0.0175);       //-выведение из списка
                mMap.addMarker(new MarkerOptions().position(buffer).title(zavedenie1.shops_name2));
                rectOptions.add(new LatLng(zavedenie1.latitude2 / 0.0175, zavedenie1.longitude2 / 0.0175));
                i++;
            }
            mMap.addPolygon(rectOptions);
        }
    }


    public void algoritm_deykstra(){
        //*******************************
        //алгоритм Дейкстра--начало
        //*******************************
        proba_algoritma1="";
        Integer SIZE = list.size();

        int[] d = new int[SIZE];// минимальное расстояние
        int[] v1 = new int[SIZE];// посещенные вершины
        int temp;
        int minindex;
        int min;

        //Инициализация вершин и расстояний
        for (int i1 = 0; i1 < SIZE; i1++) {
            d[i1] = 10000;
            v1[i1] = 1;
        }
        d[0] = 0;
        // Шаг алгоритма
        do {
            minindex = 10000;
            min = 10000;
            for (int i1 = 0; i1 < SIZE; i1++) { // Если вершину ещё не обошли и вес меньше min
                if ((v1[i1] == 1) & (d[i1] < min)) { // Переприсваиваем значения
                    min = d[i1];
                    minindex = i1;
                }
            }
            // Добавляем найденный минимальный вес
            // к текущему весу вершины
            // и сравниваем с текущим минимальным весом вершины
            if (minindex != 10000) {
                for (int i1 = 0; i1 < SIZE; i1++) {
                    if (ger[minindex][i1] > 0) {
                        temp = min + ger[minindex][i1];
                        if (temp < d[i1]) {
                            d[i1] = temp;
                        }
                    }
                }
                v1[minindex] = 0;
            }
        } while (minindex < 10000);
        // Восстановление пути
        int[] ver = new int[SIZE];// массив посещенных вершин
        int end = SIZE-1; // индекс конечной вершины = 5 - 1
        ver[0] = end + 1; // начальный элемент - конечная вершина
        int k = 1; // индекс предыдущей вершины
        int weight = d[end]; // вес конечной вершины

        while (end > 0) // пока не дошли до начальной вершины
        {
            for (int i1 = 0; i1 < SIZE; i1++) // просматриваем все вершины
            {
                if (ger[end][i1] != 0)   // если связь есть
                {
                    int temp1 = weight - ger[end][i1]; // определяем вес пути из предыдущей вершины
                    if (temp1 == d[i1]) // если вес совпал с рассчитанным
                    {                 // значит из этой вершины и был переход
                        weight = temp1; // сохраняем новый вес
                        end = i1;       // сохраняем предыдущую вершину
                        ver[k] = i1 + 1; // и записываем ее в массив
                        k++;
                    }
                }
            }
        }
        // Вывод пути (начальная вершина оказалась в конце массива из k элементов)
        int i4=0;
        for (int i1 = k - 1; i1 >= 0; i1--) {
            proba_algoritma1 += return_inform_by_id(ver[i1] - 1);
            shops_number[i4]=ver[i1] - 1;
            common_count++;
            i4++;

        }
        //***********************************
        //алгоритм Дейкстра--конец
        //***********************************
    }

//pravilno
    public void Click_d(View view){
        postroenie_puti=1;
        int [] shops_number= new int[500];//для перезаполнения исходного массива при изменении
                                          //начальных данных(алгоритм, категория)
        common_count=0;                   //-"-"-"-  защита от дурачков
        if(bool_value==0){
            zap_struktur();
            algoritm_deykstra();
        }else{algoritm_deykstra();}
        textView.setText(proba_algoritma1);
    }



    public void algoritm_komivoyagera(){
        //***********************************
        //алгоритм коммивояжера--начало
        //***********************************

        n = list.size();

        proba_algoritma1="";

        //инициализация
        s = 0;
        found = 0;
        min1 = 32767;
        for (int i3 = 0; i3 < n; i3++) m[i3] = 0;
        count = 1;
        m[0] = count;                        //считаем что поиск начинается с первого города
        search(0, ger);                        //считаем что поиск начинается с первого города


        if (found == 1)                        //если найден маршрут...
        {
            proba_algoritma1+= "Длина оптимального маршрута составит приблизительно " + (min1/100+1)+" км." + "\n";

            int c = 1;                    //номер в порядке обхода городов
            for (int i4 = 0; i4 < n; i4++)      //пробегаем по всем городам
            {
                int j1 = 0;
                while ((j1 <= n) &                //ищем следующий город в порядке обхода
                        (minm[j1] != c)) j1++;
                proba_algoritma1+=return_inform_by_id(j1);
                shops_number[common_count]=j1;
                common_count++;
                c++;
            }
            proba_algoritma1+= return_inform_by_id(minm[0]-1);    //обход завершается первым городом
            shops_number[common_count]=minm[0]-1;
            common_count++;
        } else proba_algoritma1 = "Path not found!";


        //***********************************
        //алгоритм коммивояжера--конец
        //***********************************
    }


    public String return_inform_by_id(int id){
        String buffer="";
        Zavedenie zavedenie1= (Zavedenie) list.get(id);
        buffer+=common_count+1
                +" "+zavedenie1.shops_name2+" "+zavedenie1.adress2
                +"\n";
        return buffer;
    }


    public void Click_k(View view){
            postroenie_puti = 1;
            int[] shops_number = new int[500];//для перезаполнения исходного массива при изменении
            //начальных данных(алгоритм, категория)
            common_count = 0;
            if (bool_value == 0) {
                zap_struktur();
                algoritm_komivoyagera();
            } else {
                algoritm_komivoyagera();
            }
            textView.setText(proba_algoritma1);
    }

    public void zap_struktur(){
        list.clear();
        bool_value=1;
        String shops_name1 = "";
        String category1 = "";
        String adress1 = "";
        int id1=0;
        int i=0, j=0;
        //-er
        Double latitude1=0.0;
        Double longitude1=0.0;
        Double common_sum=0.0;


        String buffer_change1="SELECT * FROM shops_table WHERE category='"+category_chenge+"'";
        String buffer_change2="SELECT * FROM shops_table";
        String buffer_common="";

        //запрос к базе данных и заполнение списка
        if(category_chenge!="все")buffer_common=buffer_change1;
        else buffer_common=buffer_change2;
        Cursor cursor = mDb.rawQuery(buffer_common, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            id1=cursor.getInt(0);                   //-выведение из базы в список
            shops_name1 = cursor.getString(1);
            adress1=cursor.getString(2);
            category1=cursor.getString(3);
            latitude1=cursor.getDouble(4);
            longitude1=cursor.getDouble(5);

            Zavedenie zavedenie= new Zavedenie(id1,shops_name1,adress1,latitude1,longitude1);
            list.add(zavedenie);
            cursor.moveToNext();
        }
        cursor.close();

        double [][] matrix = new double[list.size()][];
        int [][] matrix1= new int[list.size()][];


        //заполнение матрицы расстояний
        while (i<list.size()){                        //-выведение из списка
            Zavedenie zavedenie1= (Zavedenie) list.get(i);
            matrix1[i] = new int[list.size()];
            matrix[i] = new double[list.size()];
            j=0;
            while (j<list.size()) {
                Zavedenie zavedenie2= (Zavedenie) list.get(j);
                common_sum=Math.acos(Math.sin(zavedenie1.latitude2)*Math.sin(zavedenie2.latitude2)+
                        Math.cos(zavedenie1.latitude2)*Math.cos(zavedenie2.latitude2)*
                                Math.cos(zavedenie1.longitude2-zavedenie2.longitude2))*6371;
                matrix[i][j]=common_sum;
                double buffer=common_sum*100;
                matrix1[i][j]=(int) buffer;
                ger[i][j]=matrix1[i][j];
                j++;
            }
            i++;
        }

    }




    //поиск для алгоритма коммивояжера
    public void search(int x,int[][]matrix1)                //поиск следующего города в порядке
                                                            //обхода после города с номером Х
    {
        if ((count==n)&                //если просмотрели все города
                (matrix1[x][0]!=0)&                //из последнего города есть путь в первый город
                (s+matrix1[x][0]<min1))            //новая сумма расстояний меньше минимальной суммы
        {
            found=1;                    //маршрут найден
            min1=s+matrix1[x][0];                //изменяем: новая минимальная сумма расстояний
            for (int i=0;i<n;i++)minm[i]=m[i];//изменяем: новый минимальный путь
        }
        else
        {
            for (int i=0;i<n;i++)     //из текущего города просматриваем все города
                if ((i!=x)&                //новый город не совпадает с текущим
                        (matrix1[x][i]!=0)&            //есть прямой путь из x в i
                        (m[i]==0)&            //новый город еще не простотрен
                        (s+matrix1[x][i]<min1))    //текущая сумма не превышает минимальной
                {
                    s+=matrix1[x][i];                //наращиваем сумму
                    count++;                //количество просмотренных городав
                    m[i]=count;                //отмечаем у нового города новый номер в порядке обхода
                    search(i,matrix1);                //поиск нового города начиная с города i
                    m[i]=0;                    //возвращаем все назад
                    count--;                //-"-
                    s-=matrix1[x][i];                //-"-
                }
        }
        //конец поиска для алгоритма коммивояжера
    }

}