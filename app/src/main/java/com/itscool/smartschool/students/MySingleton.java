package com.itscool.smartschool.students;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class MySingleton {

private RequestQueue requestQueue;
private static MySingleton MInstance;
private static Context mCtx;

    private MySingleton(Context context){
         mCtx=context;
         requestQueue=getRequestQueue();
    }

    private  RequestQueue getRequestQueue(){
        if (requestQueue==null)
        requestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        return requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context) {

       if(MInstance==null){
           MInstance=new MySingleton(context);
       }
       return MInstance;
    }

    public <T> void addTORequestQue(Request<T> request){
        getRequestQueue().add(request);
    }
}
